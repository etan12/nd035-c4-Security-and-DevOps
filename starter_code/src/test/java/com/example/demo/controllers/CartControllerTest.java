package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.splunk.Receiver;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private Receiver receiver = mock(Receiver.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        ReflectionTestUtils.setField(cartController, "userRepository", userRepository);
        ReflectionTestUtils.setField(cartController, "cartRepository", cartRepository);
        ReflectionTestUtils.setField(cartController, "itemRepository", itemRepository);
        ReflectionTestUtils.setField(cartController, "receiver", receiver);
    }

    @Test
    public void add_to_cart_with_nonexistent_username() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("nonExistentUsername");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(5);

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_with_nonexistent_item() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(5);

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(mock(User.class));
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.empty());
        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void add_to_cart_happy_path() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(5);

        User sampleUser = createSampleUser();
        Item football = createSampleItem();

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(sampleUser);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.of(football));
        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_with_nonexistent_username() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("nonExistentUsername");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(5);

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_with_nonexistent_item() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        modifyCartRequest.setItemId(1);
        modifyCartRequest.setQuantity(5);

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(mock(User.class));
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.empty());
        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void remove_from_cart_happy_path() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername("username");
        // Remove 5 Footballs
        modifyCartRequest.setItemId(2);
        modifyCartRequest.setQuantity(5);

        User sampleUser = createSampleUser();
        Item football = createSampleItem();

        when(userRepository.findByUsername(modifyCartRequest.getUsername())).thenReturn(sampleUser);
        when(itemRepository.findById(modifyCartRequest.getItemId())).thenReturn(Optional.of(football));
        final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
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

    private Item createSampleItem() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Football");
        item.setDescription("Football description");
        item.setPrice(BigDecimal.valueOf(10));

        return item;
    }
}
