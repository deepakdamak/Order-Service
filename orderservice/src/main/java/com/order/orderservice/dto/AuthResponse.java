package com.order.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {
    private String token;
    private String tokenType;
    private String email;
    private Boolean active;
    private String role;
    private Long expiresIn;
    private String message;

    public AuthResponse(String token) {
        this.token = token;
        this.tokenType = "Bearer";
        this.message = "Login successful";
    }
}