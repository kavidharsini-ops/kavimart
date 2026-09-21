package com.kavimart.dao;

import com.kavimart.exception.DatabaseException;
import com.kavimart.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findByEmail(String email) throws DatabaseException;

    User create(User user) throws DatabaseException;
}