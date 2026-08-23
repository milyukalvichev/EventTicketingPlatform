package com.ticketing.pricing_microservice.service;

import com.ticketing.pricing_microservice.model.PromoCode;
import com.ticketing.pricing_microservice.repository.PromoCodeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PromoServiceTest {

    @Mock
    private PromoCodeRepository repository;

    @InjectMocks
    private PromoService promoService;

    @Test
    void validatePromoCode_ActiveValidCode_ReturnsTrue() {
        PromoCode promo = new PromoCode("SUMMER20", new BigDecimal("20.00"), true, LocalDate.now().plusMonths(1));
        when(repository.findByCodeIgnoreCaseAndActiveTrue("SUMMER20")).thenReturn(Optional.of(promo));

        PromoService.PromoValidationDTO result = promoService.validatePromoCode("SUMMER20");

        assertTrue(result.valid());
        assertEquals(new BigDecimal("20.00"), result.discountPercentage());
    }

    @Test
    void validatePromoCode_ExpiredCode_ReturnsFalse() {
        PromoCode promo = new PromoCode("EXPIRED50", new BigDecimal("50.00"), true, LocalDate.now().minusDays(1));
        when(repository.findByCodeIgnoreCaseAndActiveTrue("EXPIRED50")).thenReturn(Optional.of(promo));

        PromoService.PromoValidationDTO result = promoService.validatePromoCode("EXPIRED50");

        assertFalse(result.valid());
        assertEquals("Promo code has expired.", result.message());
    }

    @Test
    void validatePromoCode_NullOrEmptyCode_ReturnsFalse() {
        PromoService.PromoValidationDTO result = promoService.validatePromoCode("");
        assertFalse(result.valid());
        assertEquals("Promo code is empty.", result.message());
    }
}