package com.kavimart.service;

import com.kavimart.dao.JdbcUserDao;
import com.kavimart.dao.UserDao;
import com.kavimart.dto.LoginRequest;
import com.kavimart.dto.UserRegistrationRequest;
import com.kavimart.dto.UserResponseDTO;
import com.kavimart.exception.AuthenticationException;
import com.kavimart.exception.DatabaseException;
import com.kavimart.exception.DuplicateEmailException;
import com.kavimart.exception.ValidationException;
import com.kavimart.model.Role;
import com.kavimart.model.User;
import com.kavimart.util.PasswordUtil;
import com.kavimart.util.ValidationUtil;

import javax.sql.DataSource;
import java.util.Locale;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao;

    public AuthService(DataSource dataSource) {
        this(new JdbcUserDao(dataSource));
    }

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public UserResponseDTO register(UserRegistrationRequest request)
            throws ValidationException, DuplicateEmailException, DatabaseException {
        String name = ValidationUtil.requireText(request == null ? null : request.getName(), "Name", 120);
        String email = ValidationUtil.requireEmail(request == null ? null : request.getEmail());
        String password = request == null ? null : request.getPassword();
        ValidationUtil.requirePassword(password);

        Optional<User> existingUser = userDao.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new DuplicateEmailException();
        }

        Role role = parsePublicRole(request == null ? null : request.getRole());
        User createdUser = new User(0, name, email, PasswordUtil.hash(password), role, null);
        return toResponse(userDao.create(createdUser));
    }

    public UserResponseDTO login(LoginRequest request)
            throws ValidationException, AuthenticationException, DatabaseException {
        String email = ValidationUtil.requireEmail(request == null ? null : request.getEmail());
        String password = request == null ? null : request.getPassword();
        if (password == null || password.isBlank()) {
            throw new ValidationException("Password is required.");
        }

        Optional<User> user = userDao.findByEmail(email);
        if (user.isEmpty() || !PasswordUtil.matches(password, user.get().getPasswordHash())) {
            throw new AuthenticationException();
        }
        return toResponse(user.get());
    }

    private Role parsePublicRole(String value) throws ValidationException {
        if (value == null || value.isBlank()) {
            return Role.BUYER;
        }
        try {
            Role role = Role.valueOf(value.trim().toUpperCase(Locale.ROOT));
            if (role == Role.ADMIN) {
                throw new ValidationException("Admin accounts can only be created from seed data.");
            }
            return role;
        } catch (IllegalArgumentException exception) {
            throw new ValidationException("Role must be BUYER or SELLER.");
        }
    }

    private UserResponseDTO toResponse(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}