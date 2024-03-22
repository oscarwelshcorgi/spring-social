package com.example.springsocial.domain;

import com.example.springsocial.domain.eums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String picture;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String provider;

    @Builder
    public Users(Long id, String name, String email, String password, String picture, Role role, String provider) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.role = role;
        this.provider = provider;
    }

    public Users update(String name, String picture, String provider) {
        this.name = name;
        this.picture = picture;
        this.provider = provider;
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}