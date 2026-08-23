package com.ticketing.main_app.service;

import com.ticketing.main_app.dto.PromoDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

@Service
public class AdminPromoService {

    private final RestClient pricingRestClient;

    public AdminPromoService(RestClient pricingRestClient) {
        this.pricingRestClient = pricingRestClient;
    }

    public List<PromoDTO> getAllPromos() {
        try {
            return pricingRestClient.get()
                    .uri("/api/promos")
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<PromoDTO>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public PromoDTO getPromoById(Long id) {
        return pricingRestClient.get()
                .uri("/api/promos/" + id)
                .retrieve()
                .body(PromoDTO.class);
    }

    public void createPromo(PromoDTO dto) {
        pricingRestClient.post()
                .uri("/api/promos")
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void updatePromo(Long id, PromoDTO dto) {
        pricingRestClient.put()
                .uri("/api/promos/" + id)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void deletePromo(Long id) {
        pricingRestClient.delete()
                .uri("/api/promos/" + id)
                .retrieve()
                .toBodilessEntity();
    }
}