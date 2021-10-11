package com.example.demo.controllers;

import java.util.List;

import com.splunk.Receiver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {
	
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private Receiver receiver;
	
	
	@PostMapping("/submit/{username}")
	public ResponseEntity<UserOrder> submit(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			receiver.log(String.format("request: POST, method: submitOrder, status: ERROR, message: Failed to submit order for user: %s - " +
					"user does not exist", username));
			return ResponseEntity.notFound().build();
		}
		UserOrder order = UserOrder.createFromCart(user.getCart());
		orderRepository.save(order);
		receiver.log(String.format("request: POST, method: submitOrder, status: INFO, message: Submitted order for user: %s", username));
		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history/{username}")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		if(user == null) {
			receiver.log(String.format("request: GET, method: getOrdersForUser, status: ERROR, message: Failed to get order for user: %s - " +
					"user does not exist", username));
			return ResponseEntity.notFound().build();
		}
		receiver.log(String.format("request: GET, method: getOrdersForUser, status: INFO, message: Retrieved order for user: %s", username));
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
