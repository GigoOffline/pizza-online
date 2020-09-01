package de.gigo.pdfcreator.service;

import de.gigo.pdfcreator.dto.OrderProductCostDto;
import de.gigo.pdfcreator.dto.OrderProductDto;
import de.gigo.pdfcreator.kafka.producer.InvoiceAddressKafkaProducer;
import de.gigo.pdfcreator.kafka.producer.OrderProductKafkaProducer;
import de.gigo.pdfcreator.kafka.producer.ProductKafkaProducer;
import de.stea1th.commonslibrary.dto.*;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.*;

@Service
public class DataForPdf
{
    private final InvoiceAddressKafkaProducer invoiceAddressKafkaProducer;
    private final OrderProductKafkaProducer orderProductKafkaProducer;
    private final ProductKafkaProducer productKafkaProducer;

    public DataForPdf(InvoiceAddressKafkaProducer invoiceAddressKafkaProducer, OrderProductKafkaProducer orderProductKafkaProducer, ProductKafkaProducer productKafkaProducer)
    {
        this.invoiceAddressKafkaProducer = invoiceAddressKafkaProducer;
        this.orderProductKafkaProducer = orderProductKafkaProducer;
        this.productKafkaProducer = productKafkaProducer;
    }

    public Context getContextForPdf(Integer orderId, Integer personId)
    {
        PersonDto personDto  = invoiceAddressKafkaProducer.getInvoiceAddress(personId);
        AddressDto address = personDto.getAddress();
        List<OrderProductDto> orderItems = getOrderItems(orderId);
        Context ret = convertToContextForPdf(orderId, personDto, address, orderItems);
        return ret;
    }

    public List<OrderProductDto> getOrderItems(Integer orderId)
    {
        List<OrderProductDto> ret = orderProductKafkaProducer.getAllOrderProductAsList(orderId).stream()
                .map(_orderProduct -> {
                    OrderProductDto orderProductDto = productKafkaProducer.getProductCostDtoById(_orderProduct.getId().getProductCostId());
                    orderProductDto.setQuantity(_orderProduct.getQuantity());
                    orderProductDto.setTotalPrice(getTotalPricePerItem(orderProductDto));
                    return orderProductDto;
                }).collect(Collectors.toList());

        return ret;
    }

    public BigDecimal getTotalPricePerItem(OrderProductDto orderProductDto)
    {
        BigDecimal discount = BigDecimal.valueOf(100 - orderProductDto.getDiscount()).divide(BigDecimal.valueOf(100));
        BigDecimal priceWithDiscount = orderProductDto.getPrice().multiply(discount);
        BigDecimal ret = priceWithDiscount.multiply(BigDecimal.valueOf(orderProductDto.getQuantity())).setScale(2, RoundingMode.HALF_UP);
        return ret;
    }

    public Context convertToContextForPdf(Integer orderId, PersonDto personDto, AddressDto address, List<OrderProductDto> orderProducts)
    {
        BigDecimal totalPrice = orderProducts.stream()
                .map(OrderProductDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Context context = new Context();
        context.setVariable("orderId", orderId);
        context.setVariable("name", personDto.getFirstName() + " " + personDto.getLastName());
        context.setVariable("street", address.getStreet());
        context.setVariable("city", address.getZip() + " " + address.getCity());
        // Theoretisch nur notwendig wenn auserhalb von Deutschland
        context.setVariable("country", address.getCountry());
        context.setVariable("items", orderProducts);
        context.setVariable("totalPrice", totalPrice);
        return context;
    }
}
