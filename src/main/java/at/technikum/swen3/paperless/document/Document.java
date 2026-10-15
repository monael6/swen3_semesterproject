package at.technikum.swen3.paperless.document;

import at.technikum.swen3.paperless.category.Category;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "documents")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    protected Document() {}

    public Document(String title, String fileName, String contentType, Category category) {
        this.title = title;
        this.fileName = fileName;
        this.contentType = contentType;
        this.category = category;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getFileName() { return fileName; }
    public String getContentType() { return contentType; }
    public Instant getCreatedAt() { return createdAt; }
    public Category getCategory() { return category; }
    public void setTitle(String title) { this.title = title; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public void setCategory(Category category) { this.category = category; }
}
