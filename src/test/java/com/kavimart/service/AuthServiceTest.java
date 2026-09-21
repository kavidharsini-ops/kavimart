package com.kavimart.service;

import com.kavimart.dao.UserDao;
import com.kavimart.dto.LoginRequest;
import com.kavimart.dto.UserRegistrationRequest;
import com.kavimart.exception.AuthenticationException;
import com.kavimart.exception.DuplicateEmailException;
import com.kavimart.model.Role;
import com.kavimart.model.User;
import com.kavimart.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @Test
    void registersBuyerWithHashedPasswordAndSafeResponse() throws Exception {
        UserDao userDao = mock(UserDao.class);
        when(userDao.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userDao.create(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(42);
            return user;
        });

        AuthService service = new AuthService(userDao);
        UserRegistrationRequest request = request("New Buyer", "NEW@example.com", "correct horse", "BUYER");
        var response = service.register(request);

        assertEquals(42, response.getId());
        assertEquals(Role.BUYER, response.getRole());
        assertTrue(java.util.Arrays.stream(com.kavimart.dto.UserResponseDTO.class.getDeclaredFields())
                .noneMatch(field -> field.getName().equals("passwordHash")));
        verify(userDao).create(argThat(user ->
                user.getPasswordHash() != null
                        && !user.getPasswordHash().equals("correct horse")
                        && PasswordUtil.matches("correct horse", user.getPasswordHash())));
    }

    @Test
    void rejectsDuplicateEmail() throws Exception {
        UserDao userDao = mock(UserDao.class);
        when(userDao.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(new User(1, "Existing", "existing@example.com", "hash", Role.BUYER, null)));
        AuthService service = new AuthService(userDao);

        assertThrows(DuplicateEmailException.class,
                () -> service.register(request("Existing", "existing@example.com", "correct horse", "BUYER")));
        verify(userDao, never()).create(any());
    }

    @Test
    void authenticatesWithBcryptAndRejectsBadPassword() throws Exception {
        UserDao userDao = mock(UserDao.class);
        String hash = PasswordUtil.hash("correct horse");
        when(userDao.findByEmail("buyer@example.com"))
                .thenReturn(Optional.of(new User(7, "Buyer", "buyer@example.com", hash, Role.BUYER, null)));
        AuthService service = new AuthService(userDao);

        assertEquals(7, service.login(login("buyer@example.com", "correct horse")).getId());
        assertThrows(AuthenticationException.class, () -> service.login(login("buyer@example.com", "wrong password")));
    }

    private UserRegistrationRequest request(String name, String email, String password, String role) throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest();
        set(request, "name", name);
        set(request, "email", email);
        set(request, "password", password);
        set(request, "role", role);
        return request;
    }

    private LoginRequest login(String email, String password) throws Exception {
        LoginRequest request = new LoginRequest();
        set(request, "email", email);
        set(request, "password", password);
        return request;
    }

    private void set(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}