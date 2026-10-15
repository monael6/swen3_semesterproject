package at.technikum.swen3.paperless.document;

import java.util.List;

public interface DocumentService {
    DocumentResponse create(DocumentRequest request);
    List<DocumentResponse> findAll();
    DocumentResponse findById(long id);
    DocumentResponse update(long id, DocumentRequest request);
    void delete(long id);
}
