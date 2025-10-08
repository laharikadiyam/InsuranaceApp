package com.insurance.app.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.insurance.app.management.entity.Bike;

import java.util.List;

@Repository
public interface BikeRepository extends JpaRepository<Bike, Long> {
    List<Bike> findByStatus(String status);
    List<Bike> findByUser_IdAndStatus(Long userId, String status);
}
