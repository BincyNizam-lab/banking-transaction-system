package com.bincy.banking.bankingtransactionsystem.service.impl;

import com.bincy.banking.bankingtransactionsystem.entity.AppUser;
import com.bincy.banking.bankingtransactionsystem.exception.UserNotFoundException;
import com.bincy.banking.bankingtransactionsystem.exception.UsernameAlreadyExistsException;
import com.bincy.banking.bankingtransactionsystem.repository.UserRepository;
import com.bincy.banking.bankingtransactionsystem.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of IUserService providing user management operations.
 */
@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUser createUser(AppUser user) {
        if (user == null) {
            throw new IllegalArgumentException("user must not be null");
        }
        String username = user.getUsername();
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username must not be empty");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("username already exists: " + username);
        }
        return userRepository.save(user);
    }

    @Override
    public AppUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found with id: " + id));
    }

    @Override
    public Optional<AppUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("user not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
