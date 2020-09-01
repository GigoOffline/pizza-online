package de.stea1th.orderservice.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.stea1th.orderservice.entity.Order;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class PDFKafkaProducer extends KafkaProducer
{
    @Value("${pdf.print}")
    private String getPDFTopic;

    public PDFKafkaProducer(ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate, ObjectMapper objectMapper) {
        super(replyingKafkaTemplate, objectMapper);
    }

    @SneakyThrows
    public String printPDF(Order order)
    {
        String path = produce(getPDFTopic, order);
        return path;
    }
}
