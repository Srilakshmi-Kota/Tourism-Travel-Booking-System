package com.tourism.auth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "app_users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String passwordHash;
    private String phone;
    private String sessionToken;

    protected User() {}
    public User(String name, String email, String passwordHash, String phone) {
        this.name = name; this.email = email; this.passwordHash = passwordHash; this.phone = phone;
    }
    public Long getId(){ return id; }
    public String getName(){ return name; }
    public String getEmail(){ return email; }
    public String getPasswordHash(){ return passwordHash; }
    public String getPhone(){ return phone; }
    public String getSessionToken(){ return sessionToken; }
    public void setSessionToken(String token){ this.sessionToken = token; }
}
