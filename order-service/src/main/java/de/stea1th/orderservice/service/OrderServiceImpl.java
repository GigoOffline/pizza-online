package de.stea1th.orderservice.service;


import de.stea1th.orderservice.dto.TimeIntervalDto;
import de.stea1th.orderservice.entity.Order;
import de.stea1th.orderservice.kafka.producer.OrderProductKafkaProducer;
import de.stea1th.orderservice.kafka.producer.PDFKafkaProducer;
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
    private final PDFKafkaProducer pdfKafkaProducer;

    @Autowired
    public OrderServiceImpl(PersonKafkaProducer personKafkaProducer, OrderProductKafkaProducer orderProductKafkaProducer, OrderRepository orderRepository, PDFKafkaProducer pdfKafkaProducer) {
        this.personKafkaProducer = personKafkaProducer;
        this.orderProductKafkaProducer = orderProductKafkaProducer;
        this.orderRepository = orderRepository;
        this.pdfKafkaProducer = pdfKafkaProducer;
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

    public List<TimeIntervalDto> getInterval(String keycloak) {
        log.info("Getting time interval for keycloak: {}", keycloak);
        List<TimeIntervalDto> intervals = new ArrayList<>();
        Arrays.stream(TimeInterval.values()).forEach(x -> {
            intervals.add(new TimeIntervalDto(x.name(), x.getDescription()));
        });
        List<Integer> years = getCompletedYearsByPerson(keycloak);
        if(years != null) {
            years.forEach(year -> intervals.add(new TimeIntervalDto(year.toString(), year.toString())));
        }
        return intervals;
    }

    public List<Integer> getCompletedYearsByPerson(String keycloak) {
        var personId = personKafkaProducer.getPersonIdByKeycloak(keycloak);
        return orderRepository.findCompletedYearsByPersonId(personId);
    }

    public Order complete(String keycloak) {
        Order order = getUncompletedOrderByPersonKeycloak(keycloak);
        if (orderProductKafkaProducer.isOrderProductExists(order.getId())) {
            order.setCompleted(LocalDateTime.now());
            String path = pdfKafkaProducer.printPDF(order);
            order.setPdf(path);
            order = orderRepository.save(order);
            createEmptyOrder(order.getPersonId());
            log.info("Order complete for keycloak: {}", keycloak);
        }
        return order;
    }

    private Order createEmptyOrder(int personId) {
        Order order = new Order();
        order.setPersonId(personId);
        order = orderRepository.save(order);
        log.info("Creating new uncompleted order with id: {}", order.getId());
        return order;
    }

    private Order getUncompletedOrder(List<Order> orders) {
        Order order = orders.get(0);
        log.info("Existing uncompleted order with id: {}", order.getId());
        return order;
    }

    public List<Order> getOrdersByTimeValue(String keycloak, String value) {
        int personId = personKafkaProducer.getPersonIdByKeycloak(keycloak);
        List<Order> orders;
        try {
            var timeInterval = TimeInterval.valueOf(value);
            orders = orderRepository.findByPersonIdAndCompletedAfterOrderByCompletedDesc(personId, timeInterval.getTime());
            log.info("Getting orders for time interval: {}", value);
        } catch (IllegalArgumentException e) {
            orders = orderRepository.findByPersonIdAndCompletedYear(personId, value);
            log.info("Getting orders for year: {}", value);
        }
        return orders;
    }
}
