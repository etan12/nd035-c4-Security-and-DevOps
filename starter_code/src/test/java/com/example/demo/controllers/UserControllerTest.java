package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.splunk.Receiver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
    private Receiver receiver = mock(Receiver.class);

    @Before
    public void setUp() {
        userController = new UserController();
        ReflectionTestUtils.setField(userController, "userRepository", userRepository);
        ReflectionTestUtils.setField(userController, "cartRepository", cartRepository);
        ReflectionTestUtils.setField(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        ReflectionTestUtils.setField(userController, "receiver", receiver);
    }

    @Test
    public void create_user_happy_path() throws IOException {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("password");
        createUserRequest.setConfirmPassword("password");

        when(bCryptPasswordEncoder.encode("password")).thenReturn("hashedPassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("username", user.getUsername());
        assertEquals("hashedPassword", user.getPassword());
    }

    @Test
    public void create_user_with_invalid_password_length() throws IOException {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("username");
        createUserRequest.setPassword("a");
        createUserRequest.setConfirmPassword("a");

        when(bCryptPasswordEncoder.encode("a")).thenReturn("hashedPassword");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);
        assertNotNull(response);
        assertNull(response.getBody());
        assertEquals(400, response.getStatusCodeValue());
    }
}
