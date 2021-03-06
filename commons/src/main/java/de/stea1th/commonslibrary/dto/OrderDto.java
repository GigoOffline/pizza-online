package de.stea1th.commonslibrary.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto extends AbstractBaseDto {

    private LocalDateTimeDto created;

    private LocalDateTimeDto completed;

    private List<OrderProductCostDto> orders;

    private PersonDto person;
}
