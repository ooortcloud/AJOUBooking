package com.ajoubooking.demo.repository.admin;

import com.ajoubooking.demo.domain.Admin;
import com.ajoubooking.demo.dto.admin.AdminDto;
import org.springframework.stereotype.Repository;

/**
 * facade 패턴을 적용하여 service가 보았을 때 단일 repository를 DI하는 것처럼 보이게 만듦
 */
@Repository
public class AdminRepository {

    private final AdminDataRepository dataRepository;
    private final AdminQueryRepository queryRepository;

    public AdminRepository(AdminDataRepository dataRepository, AdminQueryRepository queryRepository) {
        this.dataRepository = dataRepository;
        this.queryRepository = queryRepository;
    }

    public AdminDto findByPw(String pw) {
        Admin temp = dataRepository.findByPw(pw);
        // 비밀번호가 옳지 않은 경우 예외처리
        if(temp == null)
            return null;
        return AdminDto.builder().pw(temp.getPw()).build();
    }

    public void updatePw(String newPw) {
        queryRepository.updatePw(newPw);
    }

}
