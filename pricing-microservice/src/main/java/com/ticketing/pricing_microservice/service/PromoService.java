package com.ticketing.pricing_microservice.service;

import com.ticketing.pricing_microservice.model.PromoCode;
import com.ticketing.pricing_microservice.repository.PromoCodeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class PromoService {

    private final PromoCodeRepository repository;

    public PromoService(PromoCodeRepository repository) {
        this.repository = repository;
    }

    public List<PromoCode> getAllPromos() {
        return repository.findAll();
    }

    public PromoCode getPromoById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promo code not found with ID: " + id));
    }

    public PromoCode createPromo(PromoCode promoCode) {
        promoCode.setCode(promoCode.getCode().trim().toUpperCase());
        if (repository.findByCodeIgnoreCaseAndActiveTrue(promoCode.getCode()).isPresent()) {
            throw new IllegalArgumentException("Promo code already exists: " + promoCode.getCode());
        }
        return repository.save(promoCode);
    }

    public PromoCode updatePromo(Long id, PromoCode updated) {
        PromoCode existing = getPromoById(id);
        existing.setCode(updated.getCode().trim().toUpperCase());
        existing.setDiscountPercentage(updated.getDiscountPercentage());
        existing.setActive(updated.isActive());
        existing.setExpirationDate(updated.getExpirationDate());
        return repository.save(existing);
    }

    public void deletePromo(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Promo code not found with ID: " + id);
        }
        repository.deleteById(id);
    }

    public PromoValidationDTO validatePromoCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return new PromoValidationDTO(null, false, BigDecimal.ZERO, "Promo code is empty.");
        }

        return repository.findByCodeIgnoreCaseAndActiveTrue(code.trim())
                .map(promo -> {
                    if (promo.getExpirationDate() != null && promo.getExpirationDate().isBefore(LocalDate.now())) {
                        return new PromoValidationDTO(code, false, BigDecimal.ZERO, "Promo code has expired.");
                    }
                    return new PromoValidationDTO(code, true, promo.getDiscountPercentage(), "Promo code applied successfully!");
                })
                .orElse(new PromoValidationDTO(code, false, BigDecimal.ZERO, "Invalid or inactive promo code."));
    }

    public record PromoValidationDTO(String code, boolean valid, BigDecimal discountPercentage, String message) {}
}