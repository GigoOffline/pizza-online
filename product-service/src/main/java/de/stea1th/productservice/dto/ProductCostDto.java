package de.stea1th.productservice.dto;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.stea1th.productservice.entity.Product;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class ProductCostDto {

    private Integer id;

    private String property;

    private BigDecimal price;

    private Integer discount;

    private ProductDto product;

    private boolean frozen;

}
