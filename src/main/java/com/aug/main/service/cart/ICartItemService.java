package com.aug.main.service.cart;


import com.aug.main.model.CartItem;

public interface ICartItemService {
    void addItemToCart(Long cartId, Long productId, Integer quantity);
    void removeItem(Long cartId, Long productId);
    void updateItemQuantity(Long cartId, Long productId, int quantity);
}
