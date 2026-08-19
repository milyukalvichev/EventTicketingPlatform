package com.ticketing.pricing_microservice.service;

import com.ticketing.pricing_microservice.repository.PromoCodeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class PromoService {

    private final PromoCodeRepository promoCodeRepository;

    public PromoService(PromoCodeRepository promoCodeRepository) {
        this.promoCodeRepository = promoCodeRepository;
    }

    public PromoValidationDTO validatePromoCode(String code) {
        if (code == null || code.isBlank()) {
            return new PromoValidationDTO(code, false, BigDecimal.ZERO, "Promo code cannot be blank.");
        }

        return promoCodeRepository.findByCodeIgnoreCaseAndActiveTrue(code.trim())
                .filter(promo -> promo.getExpirationDate() == null || promo.getExpirationDate().isAfter(LocalDate.now()))
                .map(promo -> new PromoValidationDTO(promo.getCode(), true, promo.getDiscountPercentage(), "Promo code applied successfully."))
                .orElseGet(() -> new PromoValidationDTO(code, false, BigDecimal.ZERO, "Invalid or expired promo code."));
    }

    public record PromoValidationDTO(String code, boolean valid, BigDecimal discountPercentage, String message) {}
}