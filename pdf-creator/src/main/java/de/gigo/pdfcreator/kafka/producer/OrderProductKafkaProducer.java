package de.gigo.pdfcreator.kafka.producer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gigo.pdfcreator.dto.OrderProductCostDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OrderProductKafkaProducer extends KafkaProducer
{
    @Value("${order-product.all}")
    private String getAllOrderProductTopic;

    public OrderProductKafkaProducer(ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate, ObjectMapper objectMapper)
    {
        super(replyingKafkaTemplate, objectMapper);
    }

    @SneakyThrows
    public List<OrderProductCostDto> getAllOrderProductAsList(int orderId)
    {
        String json = produce(getAllOrderProductTopic, orderId);
        List<OrderProductCostDto> ret = objectMapper.readValue(json, new TypeReference<List<OrderProductCostDto>>() {});
        return ret;
    }
}
