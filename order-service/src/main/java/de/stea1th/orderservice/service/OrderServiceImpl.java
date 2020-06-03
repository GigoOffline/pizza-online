package de.stea1th.orderservice.service;


import de.stea1th.orderservice.entity.Order;
import de.stea1th.orderservice.kafka.producer.OrderProductKafkaProducer;
import de.stea1th.orderservice.kafka.producer.PersonKafkaProducer;
import de.stea1th.orderservice.num.TimeInterval;
import de.stea1th.orderservice.repository.OrderRepository;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final PersonKafkaProducer personKafkaProducer;
    private final OrderProductKafkaProducer orderProductKafkaProducer;
    private final OrderRepository orderRepository;

//    @Value("${pdf-creator.create.invoice}")
//    private String pdfCreateTopic;

    @Autowired
    public OrderServiceImpl(PersonKafkaProducer personKafkaProducer, OrderProductKafkaProducer orderProductKafkaProducer, OrderRepository orderRepository) {
        this.personKafkaProducer = personKafkaProducer;
        this.orderProductKafkaProducer = orderProductKafkaProducer;
        this.orderRepository = orderRepository;
    }

    @SneakyThrows
    @Override
    public Order getUncompletedOrderByPersonKeycloak(String keycloak) {
        int personId = personKafkaProducer.getPersonIdByKeycloak(keycloak);
        return getUncompletedOrderByPersonId(personId);
    }

    public Order getUncompletedOrderByPersonId(int personId) {
        List<Order> orders = orderRepository.findByPersonIdAndCompletedIsNull(personId);
        return orders.isEmpty() ? createEmptyOrder(personId) : getUncompletedOrder(orders);
    }

    public Map<String, String> getInterval(String keycloak) {
        log.info("get time interval for keycloak {}", keycloak);
        Map<String, String> intervals = new HashMap<>();
        Arrays.stream(TimeInterval.values()).forEach(x -> {
            intervals.put(x.name(), x.getDescription());
        });
        List<Integer> years = getCompletedYearsByPerson(keycloak);
        if(years != null) {
            years.forEach(year -> intervals.put(year.toString(), year.toString()));
        }
        return intervals;
    }

//    @Override
//    public List<CompletedOrderDto> getCompletedOrders(String json) {
//        int person = personService.getByKeycloak(completedOrdersRequestDto.getKeycloak());
//        List<Order> orders;
//        try {
//            var timeInterval = TimeInterval.valueOf(completedOrdersRequestDto.getValue());
//            orders = orderRepository.findByPersonAndCompletedAfterOrderByCompletedDesc(person, timeInterval.getTime());
//        } catch (IllegalArgumentException e) {
//            orders = orderRepository.findByPersonAndCompletedYear(person, completedOrdersRequestDto.getValue());
//        }
//        return orders
//                .stream()
//                .map(this::createCompletedOrderDto)
//                .collect(Collectors.toList());
//    }
////

//    @Transactional
//    public Order completeOrder(String keycloak) {
//        Order order = complete(keycloak);
////        produceInvoiceAsPdf(order);
//        return order;
//    }
//
//    public void produceInvoiceAsPdf(Order order) {
//        CompletedOrderDto completedOrderDto = createCompletedOrderDto(order);
//        kafkaProducer.produce(pdfCreateTopic, completedOrderDto);
//    }

//    @Transactional
//    public CompletedOrderDto createCompletedOrderDto(Order order) {
//        List<ProductCost> productCostList = productCostRepository.getAllByOrderId(order.getId());
//        List<ProductCostInCartDto> dtoList = productCostList
//                .stream()
//                .map(productCost -> {
//                    var orderProductCost = orderProductCostService.get(order.getId(), productCost.getId());
//                    return productCostConverter.convertToDtoInCart(productCost, orderProductCost);
//                }).collect(Collectors.toList());
//        OrderDto orderDto = orderConverter.convertToDtoWithoutOPC(order);
//        var pdfCreatorDto = new CompletedOrderDto();
//        pdfCreatorDto.setOrderDto(orderDto);
//        pdfCreatorDto.setProductCostInCartDtoList(dtoList);
//        return pdfCreatorDto;
//    }
//
//    public String createCompletedOrderAsJson(Order order) {
//        String allOrderProductAsJson = orderProductKafkaProducer.getAllOrderProductAsJson(order.getId());
//    }

    public List<Integer> getCompletedYearsByPerson(String keycloak) {
        var personId = personKafkaProducer.getPersonIdByKeycloak(keycloak);
        return orderRepository.findCompletedYearsByPersonId(personId);
    }

    public Order complete(String keycloak) {
        Order order = getUncompletedOrderByPersonKeycloak(keycloak);
        if (orderProductKafkaProducer.isOrderProductExists(order.getId())) {
            order.setCompleted(LocalDateTime.now());
            order = orderRepository.save(order);
            createEmptyOrder(order.getPersonId());
            log.info("order complete for keycloak: {}", keycloak);
        }
        return order;
    }

    private Order createEmptyOrder(int personId) {
        Order order = new Order();
        order.setPersonId(personId);
        order = orderRepository.save(order);
        log.info("new uncompleted order with id: {} created", order.getId());
        return order;
    }

    private Order getUncompletedOrder(List<Order> orders) {
        Order order = orders.get(0);
        log.info("existing uncompleted order with id: {}", order.getId());
        return order;
    }
}
