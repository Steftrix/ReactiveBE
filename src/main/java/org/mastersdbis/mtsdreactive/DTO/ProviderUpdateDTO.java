package org.mastersdbis.mtsdreactive.DTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderUpdateDTO {

    @Size(max = 100)
    private String companyName;

    @Size(max = 255)
    private String companyAdress;

    @Size(max = 20)
    private String cif;

    private Integer serviceDomain;

    @Size(max = 50)
    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$",
             message = "IBAN must be in international format")
    private String bankIBAN;
}