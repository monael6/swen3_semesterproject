import { api } from "./api.js";

const documentList = document.querySelector("#document-list");
const documentCount = document.querySelector("#document-count");
const categoryCount = document.querySelector("#category-count");
const categorySelect = document.querySelector("#categoryId");
const message = document.querySelector("#page-message");
const documentForm = document.querySelector("#document-form");
const categoryForm = document.querySelector("#category-form");
const MAX_FILE_SIZE = 10 * 1024 * 1024; // must match spring.servlet.multipart.max-file-size

function showMessage(text, type = "success") {
    message.textContent = text;
    message.className = `message ${type}`;
}

function clearMessage() {
    message.className = "message hidden";
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function renderDocuments(documents) {
    documentCount.textContent = documents.length;

    if (documents.length === 0) {
        documentList.innerHTML = '<p class="empty-state">Noch keine Dokumente vorhanden.</p>';
        return;
    }

    documentList.innerHTML = documents.map((document) => `
        <a class="document-row" href="document.html?id=${document.id}">
            <span class="file-icon">${document.contentType === "application/pdf" ? "PDF" : "FILE"}</span>
            <span class="document-info">
                <strong>${escapeHtml(document.title)}</strong>
                <small>${escapeHtml(document.fileName)}</small>
            </span>
            <span class="category-badge">${escapeHtml(document.categoryName || "Ohne Kategorie")}</span>
            <span class="row-arrow" aria-hidden="true">›</span>
        </a>
    `).join("");
}

function renderCategories(categories) {
    categoryCount.textContent = categories.length;
    categorySelect.innerHTML = '<option value="">Keine Kategorie</option>' + categories
        .map((category) => `<option value="${category.id}">${escapeHtml(category.name)}</option>`)
        .join("");
}

async function loadDashboard() {
    clearMessage();
    try {
        const [documents, categories] = await Promise.all([
            api.getDocuments(),
            api.getCategories()
        ]);
        renderDocuments(documents);
        renderCategories(categories);
    } catch (error) {
        showMessage(`Daten konnten nicht geladen werden: ${error.message}`, "error");
        documentList.innerHTML = '<p class="empty-state">Keine Verbindung zum Server.</p>';
    }
}

function setFieldError(fieldId, text) {
    document.querySelector(`#${fieldId}-error`).textContent = text;
}

function validateDocument(title, file) {
    let valid = true;
    setFieldError("title", "");
    setFieldError("file", "");

    if (!title.trim()) {
        setFieldError("title", "Bitte einen Titel eingeben.");
        valid = false;
    }

    if (!file || file.size === 0) {
        setFieldError("file", "Bitte eine PDF-Datei auswählen.");
        valid = false;
    } else if (!file.name.toLowerCase().endsWith(".pdf")) {
        setFieldError("file", "Nur PDF-Dateien sind erlaubt.");
        valid = false;
    } else if (file.size > MAX_FILE_SIZE) {
        setFieldError("file", "Die Datei ist zu groß (max. 10 MB).");
        valid = false;
    }

    return valid;
}

documentForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(documentForm);
    const title = formData.get("title");
    const file = formData.get("file");

    if (!validateDocument(title, file)) {
        showMessage("Bitte die markierten Eingaben korrigieren.", "error");
        return;
    }

    if (!formData.get("categoryId")) {
        formData.delete("categoryId");
    }

    try {
        await api.uploadDocument(formData);
        documentForm.reset();
        await loadDashboard();
        showMessage("Dokument wurde hochgeladen.");
    } catch (error) {
        showMessage(`Dokument konnte nicht hochgeladen werden: ${error.message}`, "error");
    }
});

categoryForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const name = new FormData(categoryForm).get("name").trim();
    setFieldError("categoryName", "");

    if (!name) {
        setFieldError("categoryName", "Bitte einen Kategorienamen eingeben.");
        showMessage("Bitte die markierte Eingabe korrigieren.", "error");
        return;
    }

    try {
        await api.createCategory({ name });
        categoryForm.reset();
        await loadDashboard();
        showMessage("Kategorie wurde gespeichert.");
    } catch (error) {
        showMessage(`Kategorie konnte nicht gespeichert werden: ${error.message}`, "error");
    }
});

document.querySelector("#refresh-button").addEventListener("click", loadDashboard);
loadDashboard();
