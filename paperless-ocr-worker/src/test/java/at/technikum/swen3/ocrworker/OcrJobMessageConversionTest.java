package at.technikum.swen3.ocrworker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.MessageConverter;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The REST server sends JSON with its own type header; the worker must still read it. */
class OcrJobMessageConversionTest {
    @Test
    void readsJsonFromRestServerIntoOwnRecord() throws Exception {
        ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();
        MessageConverter converter = new RabbitConfig().jsonMessageConverter(mapper);

        String json = """
                {"documentId":7,"fileName":"invoice.pdf","contentType":"application/pdf",
                 "fileSize":42,"uploadedAt":"2026-11-25T15:00:00Z"}""";
        MessageProperties properties = new MessageProperties();
        properties.setContentType("application/json");
        properties.setHeader("__TypeId__", "at.technikum.swen3.paperless.messaging.OcrJobMessage");
        Method listener = OcrJobListener.class.getMethod("onMessage", OcrJobMessage.class);
        properties.setInferredArgumentType(listener.getGenericParameterTypes()[0]);

        OcrJobMessage job = (OcrJobMessage) converter.fromMessage(
                new Message(json.getBytes(StandardCharsets.UTF_8), properties));

        assertEquals(7L, job.documentId());
        assertEquals("invoice.pdf", job.fileName());
        assertEquals(Instant.parse("2026-11-25T15:00:00Z"), job.uploadedAt());
    }
}
