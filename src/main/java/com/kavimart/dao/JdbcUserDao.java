package com.kavimart.dao;

import com.kavimart.exception.DatabaseException;
import com.kavimart.model.Role;
import com.kavimart.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class JdbcUserDao implements UserDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUserDao.class);
    private static final String FIND_BY_EMAIL =
            "SELECT id, name, email, password_hash, role, created_at FROM users WHERE email = ?";
    private static final String INSERT_USER =
            "INSERT INTO users (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
    private final DataSource dataSource;

    public JdbcUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<User> findByEmail(String email) throws DatabaseException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }
                return Optional.of(mapUser(resultSet));
            }
        } catch (SQLException exception) {
            LOGGER.error("Unable to load user by email.", exception);
            throw new DatabaseException("Unable to access user data.", exception);
        }
    }

    @Override
    public User create(User user) throws DatabaseException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            statement.setString(4, user.getRole().name());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (!generatedKeys.next()) {
                    throw new SQLException("Database did not return a generated user id.");
                }
                user.setId(generatedKeys.getLong(1));
            }
            return user;
        } catch (SQLException exception) {
            LOGGER.error("Unable to create user.", exception);
            throw new DatabaseException("Unable to create the user account.", exception);
        }
    }

    private User mapUser(ResultSet resultSet) throws SQLException {
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        return new User(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("email"),
                resultSet.getString("password_hash"),
                Role.valueOf(resultSet.getString("role")),
                createdAt == null ? null : createdAt.toLocalDateTime()
        );
    }
}