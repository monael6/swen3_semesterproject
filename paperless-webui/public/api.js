const API_BASE = "/api";

async function request(path, options = {}) {
    // JSON bodies need a Content-Type header. For FormData (file upload) the browser
    // sets "multipart/form-data" with the correct boundary itself.
    const isJson = typeof options.body === "string";
    const response = await fetch(`${API_BASE}${path}`, {
        ...options,
        headers: {
            ...(isJson ? { "Content-Type": "application/json" } : {}),
            ...options.headers
        }
    });

    if (!response.ok) {
        let error;
        try {
            error = await response.json();
        } catch {
            error = {};
        }

        const validationMessages = Object.values(error.validationErrors || {});
        throw new Error(validationMessages[0] || error.message || `Fehler ${response.status}`);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}

export const api = {
    getDocuments: () => request("/documents"),
    getDocument: (id) => request(`/documents/${id}`),
    uploadDocument: (formData) => request("/documents", {
        method: "POST",
        body: formData
    }),
    getCategories: () => request("/categories"),
    createCategory: (category) => request("/categories", {
        method: "POST",
        body: JSON.stringify(category)
    })
};
