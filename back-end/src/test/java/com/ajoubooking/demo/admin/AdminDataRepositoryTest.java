package com.ajoubooking.demo.admin;

import com.ajoubooking.demo.domain.Admin;
import com.ajoubooking.demo.dto.admin.AdminDto;
import com.ajoubooking.demo.repository.admin.AdminDataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class AdminDataRepositoryTest {
    private AdminDataRepository adminDataRepository;

    @Autowired
    public AdminDataRepositoryTest(AdminDataRepository adminDataRepository) {
        this.adminDataRepository = adminDataRepository;
    }

    @Test
    @DisplayName("계정 조회 테스트")
    void findByAdminIdAndPwTest() {
        Admin found = adminDataRepository.findByPw("1234");
        AdminDto test = AdminDto.builder()
                .pw(found.getPw())
                .build();
        AdminDto answer = AdminDto.builder()
                .pw("1234")
                .build();

        assertThat(test).usingRecursiveComparison().isEqualTo(answer);
    }



}
