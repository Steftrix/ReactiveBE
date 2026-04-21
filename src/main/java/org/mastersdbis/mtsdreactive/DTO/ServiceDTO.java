package org.mastersdbis.mtsdreactive.DTO;

import org.mastersdbis.mtsdreactive.Entities.Service;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDTO {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String name;

    private String description;

    @NotNull
    private String domain;

    private String subdomain;

    private String username; // provider's username — used for lookup on create

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double price;

    @NotNull
    private String region;

    private Boolean active = true;

    private String acceptedPaymentMethods;

    @NotNull
    private String serviceType;

    @NotNull
    private Integer minimumBookingTime;

    public static ServiceDTO fromService(Service s) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(s.getId());
        dto.setName(s.getName());
        dto.setDescription(s.getDescription());
        dto.setDomain(s.getDomain());
        dto.setSubdomain(s.getSubdomain());
        dto.setPrice(s.getPrice());
        dto.setRegion(s.getRegion());
        dto.setActive(s.getActive());
        dto.setAcceptedPaymentMethods(s.getAcceptedPaymentMethods());
        dto.setServiceType(s.getServiceType());
        dto.setMinimumBookingTime(s.getMinimumBookingTime());
        return dto;
    }
}