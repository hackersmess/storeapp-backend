package com.storeapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UpdateUserRequest {
    
    @Email(message = "Email must be valid")
    public String email;
    
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    public String name;
    
    @Size(min = 8, message = "Password must be at least 8 characters")
    public String password;
    
    public String avatarUrl;
    
    public String bio;
    
    public String googleId;
}
