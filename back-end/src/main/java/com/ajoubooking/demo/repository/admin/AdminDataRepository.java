package com.ajoubooking.demo.repository.admin;

import com.ajoubooking.demo.domain.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDataRepository extends JpaRepository<Admin, Integer> {

    Admin findByPw(String pw);
}
