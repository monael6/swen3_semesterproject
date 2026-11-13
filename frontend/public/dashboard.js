import { api } from "./api.js";

const documentList = document.querySelector("#document-list");
const documentCount = document.querySelector("#document-count");
const categoryCount = document.querySelector("#category-count");
const categorySelect = document.querySelector("#categoryId");
const message = document.querySelector("#page-message");
const documentForm = document.querySelector("#document-form");
const categoryForm = document.querySelector("#category-form");

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

function validateDocument(data) {
    let valid = true;
    setFieldError("title", "");
    setFieldError("fileName", "");

    if (!data.title.trim()) {
        setFieldError("title", "Bitte einen Titel eingeben.");
        valid = false;
    }

    if (!data.fileName.trim()) {
        setFieldError("fileName", "Bitte einen Dateinamen eingeben.");
        valid = false;
    } else if (!data.fileName.includes(".")) {
        setFieldError("fileName", "Der Dateiname muss eine Dateiendung enthalten.");
        valid = false;
    }

    return valid;
}

documentForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    const formData = new FormData(documentForm);
    const documentData = {
        title: formData.get("title"),
        fileName: formData.get("fileName"),
        contentType: formData.get("contentType"),
        categoryId: formData.get("categoryId") ? Number(formData.get("categoryId")) : null
    };

    if (!validateDocument(documentData)) {
        showMessage("Bitte die markierten Eingaben korrigieren.", "error");
        return;
    }

    try {
        await api.createDocument(documentData);
        documentForm.reset();
        await loadDashboard();
        showMessage("Dokument wurde gespeichert.");
    } catch (error) {
        showMessage(`Dokument konnte nicht gespeichert werden: ${error.message}`, "error");
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
