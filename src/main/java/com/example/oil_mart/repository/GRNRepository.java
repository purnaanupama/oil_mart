package com.example.oil_mart.repository;

import com.example.oil_mart.dto.response.GRNResponse;
import com.example.oil_mart.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.oil_mart.model.Grn;

import java.awt.print.Pageable;
import java.util.Optional;

@Repository
public interface GRNRepository extends JpaRepository<Grn, Long> {
    // Method to find the latest GRN for getting the last GRN number
    Grn findTopByOrderByGrnIdDesc();
}
