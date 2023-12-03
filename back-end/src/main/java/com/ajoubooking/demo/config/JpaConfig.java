package com.ajoubooking.demo.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaConfig {

    @PersistenceContext  // EntityManager를 Bean으로 주입하기 위해 사용
    private EntityManager em;

    @Bean  // 메소드에서 리턴한 객체를 Spring IoC Container에서 관리할 수 있도록 인스턴스화 및 초기화하기 위해 사용
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
