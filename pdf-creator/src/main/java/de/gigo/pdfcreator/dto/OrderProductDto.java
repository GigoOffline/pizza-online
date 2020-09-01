package de.gigo.pdfcreator.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductDto implements Serializable {

    private String name;

    private String property;

    private int quantity;

    private BigDecimal price;

    private Integer discount;

    private BigDecimal totalPrice;
}
