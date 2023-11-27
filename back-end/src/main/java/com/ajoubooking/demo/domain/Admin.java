package com.ajoubooking.demo.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class Admin {
    @Id
    private Long id;

    private String adminId;
    private String pw;
}
