package com.ticketing.pricing_microservice.controller;

import com.ticketing.pricing_microservice.model.PromoCode;
import com.ticketing.pricing_microservice.service.PromoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promos")
public class PromoController {

    private final PromoService promoService;

    public PromoController(PromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping
    public List<PromoCode> getAllPromos() {
        return promoService.getAllPromos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromoCode> getPromoById(@PathVariable Long id) {
        return ResponseEntity.ok(promoService.getPromoById(id));
    }

    @PostMapping
    public ResponseEntity<PromoCode> createPromo(@RequestBody PromoCode promoCode) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promoService.createPromo(promoCode));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromoCode> updatePromo(@PathVariable Long id, @RequestBody PromoCode promoCode) {
        return ResponseEntity.ok(promoService.updatePromo(id, promoCode));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromo(@PathVariable Long id) {
        promoService.deletePromo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    public ResponseEntity<PromoService.PromoValidationDTO> validatePromo(@RequestParam String code) {
        return ResponseEntity.ok(promoService.validatePromoCode(code));
    }
}