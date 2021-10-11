package com.example.demo.controllers;

import java.util.Optional;
import java.util.stream.IntStream;

import com.splunk.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;

@RestController
@RequestMapping("/api/cart")
public class CartController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private Receiver receiver;
	
	@PostMapping("/addToCart")
	public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			receiver.log(String.format("request: POST, method: addTocart, status: ERROR, message: Failed to add to cart. ModifyCartRequest: %s - " +
					"user does not exist", request));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			receiver.log(String.format("request: POST, method: addTocart, status: ERROR, message: Failed to add to cart. ModifyCartRequest: %s - " +
					"item does not exist", request));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.addItem(item.get()));
		cartRepository.save(cart);
		receiver.log(String.format("request: POST, method: addTocart, status: INFO, message: Successfully added to cart. ModifyCartRequest: %s - ", request));
		return ResponseEntity.ok(cart);
	}
	
	@PostMapping("/removeFromCart")
	public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
		User user = userRepository.findByUsername(request.getUsername());
		if(user == null) {
			receiver.log(String.format("request: POST, method: removeFromcart, status: ERROR, message: Failed to remove from cart. ModifyCartRequest: %s - " +
					"user does not exist", request));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Optional<Item> item = itemRepository.findById(request.getItemId());
		if(!item.isPresent()) {
			receiver.log(String.format("request: POST, method: removeFromcart, status: ERROR, message: Failed to remove from cart. ModifyCartRequest: %s" +
					"item does not exist", request));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
		Cart cart = user.getCart();
		IntStream.range(0, request.getQuantity())
			.forEach(i -> cart.removeItem(item.get()));
		cartRepository.save(cart);
		receiver.log(String.format("request: POST, method: removeFromcart, status: INFO, message: Successfully removed from cart. ModifyCartRequest: %s", request));
		return ResponseEntity.ok(cart);
	}
		
}
