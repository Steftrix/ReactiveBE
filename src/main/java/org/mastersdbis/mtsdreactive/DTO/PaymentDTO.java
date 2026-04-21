package org.mastersdbis.mtsdreactive.DTO;

import org.mastersdbis.mtsdreactive.Entities.Payment;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {

    private Integer id;

    @NotNull
    private Integer bookingId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private Double amount;

    @NotNull
    private String paymentMethod;

    @NotNull
    private String paymentState;

    private LocalDate paymentDate;

    public static PaymentDTO fromPayment(Payment p) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(p.getId());
        dto.setBookingId(p.getBookingId());
        dto.setAmount(p.getAmount());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setPaymentState(p.getPaymentState());
        dto.setPaymentDate(p.getPaymentDate());
        return dto;
    }

    public Payment toPayment() {
        Payment p = new Payment();
        p.setId(this.id);
        p.setBookingId(this.bookingId);
        p.setAmount(this.amount);
        p.setPaymentMethod(this.paymentMethod);
        p.setPaymentState(this.paymentState);
        p.setPaymentDate(this.paymentDate);
        return p;
    }
}