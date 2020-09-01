package de.stealth.personservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.stealth.personservice.entity.Person;
import de.stealth.personservice.service.PersonService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ReadKafkaConsumer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PersonService personService;

    public ReadKafkaConsumer(PersonService personService) {
        this.personService = personService;
    }

    @SneakyThrows
    @KafkaListener(topics="${person.get}")
    @SendTo
    public String getPerson(String message) {
        log.info("Receiving with Kafka keycloak: {}", message);
        Person person = personService.getByKeycloak(message);
        return objectMapper.writeValueAsString(person);
    }


    @SneakyThrows
    @KafkaListener(topics="${person.invoiceAddress}")
    @SendTo
    public String getInvoiceAddress(String message) {
        log.info("Receiving with Kafka keycloak: {}", message);
        Person person = personService.get(Integer.valueOf(message));
        return objectMapper.writeValueAsString(person);
    }

}
