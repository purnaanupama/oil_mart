package com.example.oil_mart.repository;

import com.example.oil_mart.model.GrnItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GRNItemRepository extends JpaRepository<GrnItem, Long> {

    List<GrnItem> findByItemIdIn(List<Long> itemIds);
}
