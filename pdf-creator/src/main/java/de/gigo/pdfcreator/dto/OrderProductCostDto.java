package de.gigo.pdfcreator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductCostDto {

    protected OrderProductCostPKDto id;

    private Integer quantity;

    private BigDecimal price;

    private Integer discount;

}
