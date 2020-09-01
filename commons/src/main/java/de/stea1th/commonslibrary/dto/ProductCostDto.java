package de.stea1th.commonslibrary.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ProductCostDto extends AbstractBaseDto {

    private Integer productId;

    private String property;

    private BigDecimal price;

    private Integer discount;

    private boolean frozen;

}
