package com.ticketing.main_app.dto;

import java.math.BigDecimal;

public class PromoResponseDTO {

    private String code;
    private boolean valid;
    private BigDecimal discountPercentage;
    private String message;

    public PromoResponseDTO() {}

    public PromoResponseDTO(String code, boolean valid, BigDecimal discountPercentage, String message) {
        this.code = code;
        this.valid = valid;
        this.discountPercentage = discountPercentage;
        this.message = message;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }
    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}