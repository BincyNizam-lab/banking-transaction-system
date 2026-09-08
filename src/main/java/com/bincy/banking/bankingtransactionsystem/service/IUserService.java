package com.bincy.banking.bankingtransactionsystem.service;

import com.bincy.banking.bankingtransactionsystem.entity.AppUser;
import com.bincy.banking.bankingtransactionsystem.exception.UserNotFoundException;
import com.bincy.banking.bankingtransactionsystem.exception.UsernameAlreadyExistsException;

import java.util.Optional;

/**
 * Service interface for user management operations.
 * Provides user creation, retrieval, and deletion functionality.
 */
public interface IUserService {

    /**
     * Creates a new user in the system.
     *
     * @param user the AppUser object to create
     * @return the created AppUser with ID populated
     * @throws IllegalArgumentException if user is null or username is empty
     * @throws UsernameAlreadyExistsException if username already exists
     */
    AppUser createUser(AppUser user);

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id the user ID
     * @return the AppUser object
     * @throws UserNotFoundException if user not found
     */
    AppUser getUserById(Long id);

    /**
     * Finds a user by their username.
     *
     * @param username the username to search for
     * @return Optional containing the AppUser if found, empty otherwise
     */
    Optional<AppUser> findByUsername(String username);

    /**
     * Deletes a user by their ID.
     *
     * @param id the user ID to delete
     * @throws UserNotFoundException if user not found
     */
    void deleteUser(Long id);
}
