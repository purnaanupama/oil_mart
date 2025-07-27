package com.example.oil_mart.repository;

import com.example.oil_mart.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByItemBrand_Id(Integer brandId);
}
