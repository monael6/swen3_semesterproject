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

async function loadDocument() {
    const id = new URLSearchParams(window.location.search).get("id");

    if (!id || !/^\d+$/.test(id)) {
        detail.innerHTML = '<div class="message error">Ungültige Dokument-ID.</div>';
        return;
    }

    try {
        const documentData = await api.getDocument(id);
        detail.innerHTML = `
            <p class="eyebrow">Dokument #${documentData.id}</p>
            <h1>${escapeHtml(documentData.title)}</h1>
            <dl class="detail-grid">
                <div><dt>Dateiname</dt><dd>${escapeHtml(documentData.fileName)}</dd></div>
                <div><dt>Content-Type</dt><dd>${escapeHtml(documentData.contentType)}</dd></div>
                <div><dt>Kategorie</dt><dd>${escapeHtml(documentData.categoryName || "Ohne Kategorie")}</dd></div>
                <div><dt>Erstellt</dt><dd>${escapeHtml(formatDate(documentData.createdAt))}</dd></div>
            </dl>
        `;
    } catch (error) {
        detail.innerHTML = `<div class="message error">Dokument konnte nicht geladen werden: ${escapeHtml(error.message)}</div>`;
    }
}

loadDocument();
