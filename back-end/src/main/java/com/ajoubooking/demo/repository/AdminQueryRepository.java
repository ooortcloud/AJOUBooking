package com.ajoubooking.demo.repository;

import com.ajoubooking.demo.domain.QAdmin;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.ajoubooking.demo.domain.QAdmin.admin;

@Repository
@Transactional
public class AdminQueryRepository {

    private EntityManager em;
    private JPAQueryFactory queryFactory;

    public AdminQueryRepository(EntityManager em, JPAQueryFactory queryFactory) {
        this.em = em;
        this.queryFactory = queryFactory;
    }

    public void updatePw(String newPw) {
        queryFactory.update(admin)
                .set(admin.pw, newPw)
                .where(admin.id.eq(1))
                .execute();

        /*  DB와 정합성을 맞추기 위해, 벌크 연산 이후 영속성 컨텍스트 초기화  << 아래 코드는 transaction commit 시 자동으로 적용됨
        em.flush();
        em.clear();
         */
    }
}
