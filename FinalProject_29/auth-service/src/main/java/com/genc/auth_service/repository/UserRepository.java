package com.genc.auth_service.repository;

import com.genc.auth_service.model.Role;
import com.genc.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByEmail(String email);
    
    Boolean existsByPhone(String phone);
    
    List<User> findByRole(Role role);
    
    List<User> findByActive(Boolean active);
}

