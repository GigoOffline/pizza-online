package de.stea1th.persist.kafka;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.stea1th.commonslibrary.dto.OrderProductCostDto;
import de.stea1th.persist.service.OrderProductCostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderProductCostKafkaConsumer {

    private final OrderProductCostService orderProductCostService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public OrderProductCostKafkaConsumer(OrderProductCostService orderProductCostService) {
        this.orderProductCostService = orderProductCostService;
    }

    @KafkaListener(topics = "${order-product.cart.add}", groupId = "pizza-online")
    public void processSaveOrderProductCost(String message) {
        log.info("received message = {}", message);
        try {
            OrderProductCostDto orderProductCostDto = objectMapper.readValue(message, OrderProductCostDto.class);
            log.info("order-product dto: {} prepared to save", objectMapper.writeValueAsString(orderProductCostDto));
            orderProductCostService.save(orderProductCostDto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
