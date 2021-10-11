package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.splunk.Receiver;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);
    private Receiver receiver = mock(Receiver.class);

    @Mock
    private UserOrder userOrder;

    @Before
    public void setUp() {
        orderController = new OrderController();
        ReflectionTestUtils.setField(orderController, "userRepository", userRepository);
        ReflectionTestUtils.setField(orderController, "orderRepository", orderRepository);
        ReflectionTestUtils.setField(orderController, "receiver", receiver);
    }

    @Test
    public void submit_order_with_nonexistent_username() {

        when(userRepository.findByUsername("nonExistentUsername")).thenReturn(null);

        final ResponseEntity<UserOrder> response = orderController.submit("nonExistentUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void submit_order_happy_path() {

        when(userRepository.findByUsername("username")).thenReturn(createSampleUser());

        final ResponseEntity<UserOrder> response = orderController.submit("username");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void get_orders_with_nonexistent_username() {

        when(userRepository.findByUsername("nonExistentUsername")).thenReturn(null);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonExistentUsername");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void get_orders_happy_path() {
        User sampleUser = createSampleUser();
        when(userRepository.findByUsername(sampleUser.getUsername())).thenReturn(sampleUser);
        when(orderRepository.findByUser(sampleUser)).thenReturn(Arrays.asList(userOrder));

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("username");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    private User createSampleUser() {
        User user = new User();
        user.setId(1);
        user.setUsername("username");
        user.setPassword("password");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setId(1L);
        cart.setTotal(BigDecimal.valueOf(5));

        Item item = new Item();
        item.setId(1L);
        item.setName("Basketball");
        item.setDescription("Basketball description");
        item.setPrice(BigDecimal.valueOf(5));
        cart.setItems(new ArrayList<>(Arrays.asList(item)));
        user.setCart(cart);

        return user;
    }
}
