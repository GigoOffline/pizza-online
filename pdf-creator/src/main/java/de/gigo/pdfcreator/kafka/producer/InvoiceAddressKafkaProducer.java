package de.gigo.pdfcreator.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.stea1th.commonslibrary.dto.PersonDto;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class InvoiceAddressKafkaProducer extends KafkaProducer
{
    @Value("${person.invoiceAddress}")
    private String getInvoiceAddressTopic;

    public InvoiceAddressKafkaProducer(ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate, ObjectMapper objectMapper)
    {
        super(replyingKafkaTemplate, objectMapper);
    }

    @SneakyThrows
    public PersonDto getInvoiceAddress(int personId)
    {
        String ret = produce(getInvoiceAddressTopic, personId);
        PersonDto personDto = objectMapper.readValue(ret, PersonDto.class);
        return personDto;
    }
}
