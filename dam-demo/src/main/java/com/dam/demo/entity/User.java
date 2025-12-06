package com.dam.demo.entity;

import com.dam.framework.annotations.*;

/**
 * Example User entity for demonstrating DAM Framework.
 */
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "username", nullable = false, length = 50)
    private String username;
    
    @Column(name = "email", unique = true)
    private String email;
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "status")
    private String status;
    
    // Default constructor (required)
    public User() {
    }
    
    // Constructor with fields
    public User(String username, String email, Integer age) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.status = "active";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", status='" + status + '\'' +
                '}';
    }
}
