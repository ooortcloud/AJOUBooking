package com.ajoubooking.demo.repository;

import com.ajoubooking.demo.domain.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * facade 패턴을 적용하여 service가 보았을 때 단일 repository를 DI하는 것처럼 보이게 만듦
 */
@Repository
public class AdminRepository {
    @Autowired  // Spring Container에 등록된 @Repository들을 자동으로 DI 시킴
    private AdminDataRepository dataRepository;
    @Autowired
    private AdminQueryRepository queryRepository;

    public Admin findByPw(String pw) {
        return dataRepository.findByPw(pw);
    }

    public void updatePw(String newPw) {
        queryRepository.updatePw(newPw);
    }
}
