package de.stea1th.persist.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "description"}, name = "product_unique_name_description_index")})
public class Product extends AbstractBaseEntity {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotBlank
    private String picture;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER)
    private List<ProductCost> productCostList;
}
