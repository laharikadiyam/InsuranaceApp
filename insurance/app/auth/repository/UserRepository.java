package com.insurance.app.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


//Entity package
import com.insurance.app.auth.entity.Roles;
import com.insurance.app.auth.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    List<Users> findAllByRoleAndIsActiveFalse(Roles role);
    Optional<Users> findById(Long id);
}