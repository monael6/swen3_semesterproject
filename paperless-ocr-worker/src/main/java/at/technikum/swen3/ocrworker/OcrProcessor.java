package at.technikum.swen3.ocrworker;

/** Performs OCR for one document. Sprint 3: only logs; Sprint 4: MinIO + Tesseract. */
public interface OcrProcessor {
    void process(OcrJobMessage job);
}
