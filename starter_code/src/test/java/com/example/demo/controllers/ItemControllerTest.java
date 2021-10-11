package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        ReflectionTestUtils.setField(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItems() {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getItemById() {
        long id = 1;
        Item item = new Item();
        when(itemRepository.findById(id)).thenReturn(java.util.Optional.of(item));
        final ResponseEntity<Item> response = itemController.getItemById(id);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void getItemsByName() {
        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item());
        when(itemRepository.findByName("Basketball")).thenReturn(itemList);
        final ResponseEntity<List<Item>> response = itemController.getItemsByName("Basketball");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

}
