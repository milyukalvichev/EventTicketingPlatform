package com.ticketing.main_app.dto;

import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PromoDTO {

    private Long id;

    @NotBlank(message = "Promo code is required.")
    @Size(min = 3, max = 20, message = "Code must be between 3 and 20 characters.")
    private String code;

    @NotNull(message = "Discount percentage is required.")
    @DecimalMin(value = "1.00", message = "Discount must be at least 1%.")
    @DecimalMax(value = "90.00", message = "Discount cannot exceed 90%.")
    private BigDecimal discountPercentage;

    private boolean active = true;

    @NotNull(message = "Expiration date is required.")
    @Future(message = "Expiration date must be in the future.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expirationDate;

    public PromoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
}