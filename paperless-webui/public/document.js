import { api } from "./api.js";

const detail = document.querySelector("#document-detail");

function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function formatDate(value) {
    if (!value) return "–";
    return new Intl.DateTimeFormat("de-AT", {
        dateStyle: "medium",
        timeStyle: "short"
    }).format(new Date(value));
}

function formatSize(bytes) {
    if (bytes == null) return "–";
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

async function loadDocument() {
    const id = new URLSearchParams(window.location.search).get("id");

    if (!id || !/^\d+$/.test(id)) {
        detail.innerHTML = '<div class="alert alert-danger mb-0">Ungültige Dokument-ID.</div>';
        return;
    }

    try {
        const documentData = await api.getDocument(id);
        detail.innerHTML = `
            <p class="text-primary text-uppercase fw-bold small mb-1">Dokument #${documentData.id}</p>
            <h1 class="h2 fw-bold mb-4">${escapeHtml(documentData.title)}</h1>
            <dl class="row mb-0">
                <dt class="col-sm-4">Dateiname</dt><dd class="col-sm-8">${escapeHtml(documentData.fileName)}</dd>
                <dt class="col-sm-4">Content-Type</dt><dd class="col-sm-8">${escapeHtml(documentData.contentType)}</dd>
                <dt class="col-sm-4">Größe</dt><dd class="col-sm-8">${escapeHtml(formatSize(documentData.fileSize))}</dd>
                <dt class="col-sm-4">Kategorie</dt><dd class="col-sm-8">${escapeHtml(documentData.categoryName || "Ohne Kategorie")}</dd>
                <dt class="col-sm-4">Erstellt</dt><dd class="col-sm-8 mb-0">${escapeHtml(formatDate(documentData.createdAt))}</dd>
            </dl>
        `;
    } catch (error) {
        detail.innerHTML = `<div class="alert alert-danger mb-0">Dokument konnte nicht geladen werden: ${escapeHtml(error.message)}</div>`;
    }
}

loadDocument();
