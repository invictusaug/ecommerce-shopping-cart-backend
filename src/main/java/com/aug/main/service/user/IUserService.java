package com.aug.main.service.user;

import com.aug.main.dto.UserDto;
import com.aug.main.model.User;
import com.aug.main.request.CreateUserRequest;
import com.aug.main.request.UserUpdateRequest;

public interface IUserService {
    User getUserById(Long userId);
    User createUser(CreateUserRequest user);
    User updateUser(UserUpdateRequest request, Long userId);
    void deleteUser(Long userId);

    UserDto convertUserToDto(User user);

    User getAuthenticatedUser();
}
