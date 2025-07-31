package com.example.oil_mart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.oil_mart.model.Grn;

@Repository
public interface GRNRepository extends JpaRepository<Grn, Long> {
    // Method to find the latest GRN for getting the last GRN number
    Grn findTopByOrderByGrnIdDesc();
}
