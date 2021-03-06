package de.stea1th.commonslibrary.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AddressDto extends AbstractBaseDto {

    private String street;
    private String zip;
    private String city;
    private String country;

}
