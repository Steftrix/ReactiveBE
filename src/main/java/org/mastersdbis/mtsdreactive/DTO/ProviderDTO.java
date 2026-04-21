package org.mastersdbis.mtsdreactive.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mastersdbis.mtsdreactive.Entities.Provider;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderDTO {

    private Integer id;

    @NotNull(message = "User ID is required")
    private Integer userId;

    @Size(max = 20)
    @Pattern(regexp = "^RO[0-9]{1,9}[0-9]$",
             message = "CIF must be in format RO followed by digits")
    private String cif;

    @Size(max = 100)
    private String companyName;

    @Size(max = 255)
    private String companyAdress;

    @NotNull(message = "Service domain is required")
    private Integer serviceDomain;

    @Size(max = 50)
    @Pattern(regexp = "^[A-Z]{2}\\d{2}[A-Z0-9]{1,30}$",
             message = "IBAN must be in international format")
    private String bankIBAN;

    private String validationStatus;
    private Integer approvedByUserId;

    public static ProviderDTO fromProvider(Provider p) {
        ProviderDTO dto = new ProviderDTO();
        dto.setId(p.getId());
        dto.setUserId(p.getId()); // shared PK
        dto.setCif(p.getCif());
        dto.setCompanyName(p.getCompanyName());
        dto.setCompanyAdress(p.getCompanyAdress());
        dto.setServiceDomain(p.getServiceDomain());
        dto.setBankIBAN(p.getBankIBAN());
        dto.setValidationStatus(p.getValidationStatus());
        dto.setApprovedByUserId(p.getApprovedById());
        return dto;
    }
}