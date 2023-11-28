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
    private Integer id;

    private String pw;
}
