package at.technikum.swen3.ocrworker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** "Empty" OCR worker for Sprint 3: confirms that the job arrived. Replaced by real OCR in Sprint 4. */
@Service
public class LoggingOcrProcessor implements OcrProcessor {
    private static final Logger log = LoggerFactory.getLogger(LoggingOcrProcessor.class);

    @Override
    public void process(OcrJobMessage job) {
        log.info("OCR job received: documentId={} fileName={} contentType={} size={} bytes uploadedAt={}",
                job.documentId(), job.fileName(), job.contentType(), job.fileSize(), job.uploadedAt());
        log.info("OCR for document id={} not implemented yet (Sprint 4)", job.documentId());
    }
}
