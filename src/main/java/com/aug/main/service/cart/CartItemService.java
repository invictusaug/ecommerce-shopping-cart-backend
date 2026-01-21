package com.aug.main.service.cart;

import com.aug.main.exceptions.ResourceNotFoundException;
import com.aug.main.model.Cart;
import com.aug.main.model.CartItem;
import com.aug.main.model.Product;
import com.aug.main.repository.CartItemRepository;
import com.aug.main.repository.CartRepository;
import com.aug.main.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final IProductService productService;
    private final ICartService cartService;

    @Override
    public void addItemToCart(Long cartId, Long productId, Integer quantity) {
        Cart cart = cartService.getCart(cartId);
        Product product = productService.getProductById(productId);
        CartItem cartItem = cart.getItems()
                .stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst().orElse(new CartItem());
        if(cartItem.getId() == null) {
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setUnitPrice(product.getPrice());
        } else  {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeItem(Long cartId, Long productId) {
        Cart cart = cartService.getCart(cartId);
        CartItem itemToRemove = getCartItem(cartId, productId);
        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cartId, productId).orElseThrow(() -> new ResourceNotFoundException("Cart item with"));
        if (!cartItem.getCart().getId().equals(cartId)) {
            throw new IllegalStateException("Item does not belong to this cart");
        }
        cartItem.setQuantity(quantity);
        cartItem.setUnitPrice(cartItem.getProduct().getPrice());
        cartItem.setTotalPrice();
        Cart cart = cartItem.getCart();
//        cart.getItems()
//                .stream()
//                .filter(i -> i.getProduct().getId().equals(productId))
//                .findFirst()
//                .ifPresent(item -> {
//                    item.setQuantity(quantity);
//                    item.setUnitPrice(item.getProduct().getPrice());
//                    item.setTotalPrice();
//                    cartItemRepository.save(item);
//                });

        BigDecimal totalAmount = cart.getItems()
                .stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalAmount(totalAmount);
//        cartRepository.save(cart);
    }

    public CartItem getCartItem(Long cartId,  Long productId) {
        Cart cart = cartService.getCart(cartId);
        return cart.getItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}
