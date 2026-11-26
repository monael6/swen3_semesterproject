package at.technikum.swen3.paperless.document;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    DocumentResponse upload(DocumentUploadRequest request, MultipartFile file);
    List<DocumentResponse> findAll();
    DocumentResponse findById(long id);
    DocumentResponse update(long id, DocumentRequest request);
    void delete(long id);
}
