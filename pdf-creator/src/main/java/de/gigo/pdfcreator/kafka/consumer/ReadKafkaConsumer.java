package de.gigo.pdfcreator.kafka.consumer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.gigo.pdfcreator.service.DataForPdf;
import de.gigo.pdfcreator.service.GeneratePDF;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

@Component
@Slf4j
public class ReadKafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DataForPdf dataForPdf;
    private final GeneratePDF generatePDF;

    public ReadKafkaConsumer(DataForPdf dataForPdf, GeneratePDF generatePDF)
    {
        this.dataForPdf = dataForPdf;
        this.generatePDF = generatePDF;
    }

    @SneakyThrows
    @KafkaListener(topics = "${pdf.print}")
    @SendTo
    public String pdfPrint(String message) {
        log.info("Receiving keycloak: {}", message);
        JsonNode rootNode = objectMapper.readTree(message);
        Context contextForPdf = dataForPdf.getContextForPdf(rootNode.get("id").asInt(), rootNode.get("personId").asInt());
        String template = generatePDF.parseThymeleafTemplate(contextForPdf, "templates/thymeleaf_template");
        String path = generatePDF.generatePdfFromHtml(template, rootNode.get("id").asText());
        return objectMapper.writeValueAsString("test");
    }
}
