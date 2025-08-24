package com.example.oil_mart.repository;

import com.example.oil_mart.model.Return;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReturnRepository extends JpaRepository<Return, Long> {
    Optional<Return> findTopByOrderByIdDesc();
}
