package com.order.orderservice.service;

import com.order.orderservice.dto.UserCreateRequest;
import com.order.orderservice.dto.UserResponse;
import java.util.List;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);
    UserResponse getUserById(Long id);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsers();
    UserResponse updateUser(Long id, UserCreateRequest request);
    void deleteUser(Long id);
}