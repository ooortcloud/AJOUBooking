package com.ajoubooking.demo.admin;

import com.ajoubooking.demo.domain.Admin;
import com.ajoubooking.demo.dto.admin.AdminDto;
import com.ajoubooking.demo.repository.AdminRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class AdminRepositoryTest {
    private AdminRepository adminRepository;

    @Autowired
    public AdminRepositoryTest(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Test
    @DisplayName("계정 조회 테스트")
    void findByAdminIdAndPwTest() {
        Admin found = adminRepository.findByPw("1234");
        AdminDto test = AdminDto.builder()
                .pw(found.getPw())
                .build();
        AdminDto answer = AdminDto.builder()
                .pw("1234")
                .build();

        assertThat(test).usingRecursiveComparison().isEqualTo(answer);
    }



}
