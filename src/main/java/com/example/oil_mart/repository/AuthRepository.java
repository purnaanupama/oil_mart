package com.example.oil_mart.repository;

import com.example.oil_mart.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
