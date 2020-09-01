package de.gigo.pdfcreator.kafka.producer;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.gigo.pdfcreator.dto.OrderProductDto;
import de.stea1th.commonslibrary.dto.ProductCostDto;
import de.stea1th.commonslibrary.dto.ProductDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ProductKafkaProducer extends KafkaProducer
{
    @Value("${product-cost.get}")
    private String getProductCostTopic;

    @Value("${product-cost.get.product}")
    private String getProductTopic;

    public ProductKafkaProducer(ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate, ObjectMapper objectMapper)
    {
        super(replyingKafkaTemplate, objectMapper);
    }

    @SneakyThrows
    public OrderProductDto getProductCostDtoById(int productCostId) {
        logMessage(productCostId, getProductCostTopic);
        String json =  produce(getProductCostTopic, productCostId);
        JsonNode rootNode = objectMapper.readTree(json);
        OrderProductDto ret = objectMapper.readValue(json, OrderProductDto.class);
        ret.setName(rootNode.get("product").get("name").asText());
        return ret;
    }

    private void logMessage(Object message, String topic) {
        log.info("Sending message {} to {} topic", message, topic);
    }
}
