package com.ticketing.pricing_microservice.controller;

import com.ticketing.pricing_microservice.service.PromoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promos")
public class PromoController {

    private final PromoService promoService;

    public PromoController(PromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping("/validate")
    public ResponseEntity<PromoService.PromoValidationDTO> validateCode(@RequestParam String code) {
        return ResponseEntity.ok(promoService.validatePromoCode(code));
    }
}