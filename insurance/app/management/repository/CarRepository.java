package com.insurance.app.management.repository;

import com.insurance.app.management.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByStatus(String status);
    List<Car> findByUser_IdAndStatus(Long userId, String status);
}
