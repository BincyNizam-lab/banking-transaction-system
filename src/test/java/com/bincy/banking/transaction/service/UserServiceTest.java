package com.bincy.banking.transaction.service;

import com.bincy.banking.bankingtransactionsystem.entity.AppUser;
import com.bincy.banking.bankingtransactionsystem.exception.UserNotFoundException;
import com.bincy.banking.bankingtransactionsystem.exception.UsernameAlreadyExistsException;
import com.bincy.banking.bankingtransactionsystem.repository.UserRepository;
import com.bincy.banking.bankingtransactionsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private AppUser exampleUser;

    @BeforeEach
    void setUp() {
        exampleUser = AppUser.builder()
                .id(1L)
                .username("alice")
                .password("secret")
                .role("USER")
                .build();
    }

    @Test
    void createUser_success() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.empty());
        when(userRepository.save(exampleUser)).thenReturn(exampleUser);

        AppUser created = userService.createUser(exampleUser);

        assertNotNull(created);
        assertEquals("alice", created.getUsername());
        verify(userRepository).findByUsername("alice");
        verify(userRepository).save(exampleUser);
    }

    @Test
    void createUser_existingUsername_throws() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(exampleUser));

        assertThrows(UsernameAlreadyExistsException.class, () -> userService.createUser(exampleUser));
        verify(userRepository).findByUsername("alice");
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_nullUser_throws() {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void createUser_emptyUsername_throws() {
        AppUser u = AppUser.builder().username("  ").password("p").build();
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(u));
        verify(userRepository, never()).findByUsername(anyString());
    }

    @Test
    void getUserById_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(exampleUser));

        AppUser found = userService.getUserById(1L);

        assertEquals(1L, found.getId());
        assertEquals("alice", found.getUsername());
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void findByUsername_returnsOptional() {
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(exampleUser));

        Optional<AppUser> maybe = userService.findByUsername("alice");

        assertTrue(maybe.isPresent());
        assertEquals("alice", maybe.get().getUsername());
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_notFound_throws() {
        when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(2L));
        verify(userRepository).existsById(2L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}
