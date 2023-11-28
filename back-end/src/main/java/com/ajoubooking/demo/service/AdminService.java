package com.ajoubooking.demo.service;

import com.ajoubooking.demo.domain.Admin;
import com.ajoubooking.demo.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public boolean validateRequestLogin(String pw){
        Admin foundAdmin = adminRepository.findByPw(pw);
        // 계정을 찾을 수 없으면 예외처리
        if(foundAdmin == null) return false;
        return true;
    }


}
