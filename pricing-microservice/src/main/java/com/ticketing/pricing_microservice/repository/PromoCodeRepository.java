package com.ticketing.pricing_microservice.repository;

import com.ticketing.pricing_microservice.model.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {
    Optional<PromoCode> findByCodeIgnoreCaseAndActiveTrue(String code);
}