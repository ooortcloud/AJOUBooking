package com.ajoubooking.demo.repository;

import com.ajoubooking.demo.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
