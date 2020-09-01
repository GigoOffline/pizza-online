package de.gigo.pdfcreator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderProductCostPKDto
{

    private Integer orderId;

    private Integer productCostId;

}
