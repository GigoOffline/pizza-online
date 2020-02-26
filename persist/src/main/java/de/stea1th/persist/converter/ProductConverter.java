package de.stea1th.persist.converter;

import de.stea1th.commonslibrary.component.PicConverter;
import de.stea1th.commonslibrary.dto.ProductCostDto;
import de.stea1th.commonslibrary.dto.ProductDto;
import de.stea1th.persist.entity.Product;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProductConverter {

    private final ProductCostConverter productCostConverter;

    private PicConverter picConverter;

    public ProductConverter(@Lazy ProductCostConverter productCostConverter, PicConverter picConverter) {
        this.productCostConverter = productCostConverter;
        this.picConverter = picConverter;
    }

    public ProductDto convertToDtoWithoutProductCostList(Product product) {
        var productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        try {
            String base64 = picConverter.encodeFileFromResourcesToBase64(product.getPicture());
            productDto.setPicture(base64);
        } catch (IOException e) {
            productDto.setPicture(product.getPicture());
        }
        return productDto;
    }

    public ProductDto convertToDto(Product product) {
        var productDto = convertToDtoWithoutProductCostList(product);
        List<ProductCostDto> productCostDtoList = product.getProductCostList()
                .stream()
                .map(productCostConverter::convertToDto)
                .collect(Collectors.toList());
        productDto.setProductCostList(productCostDtoList);
        return productDto;
    }

}
