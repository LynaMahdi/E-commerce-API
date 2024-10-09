package com.example.tp2_api_rest.ecommerceapi.service;


import com.example.tp2_api_rest.ecommerceapi.entity.Cart;
import com.example.tp2_api_rest.ecommerceapi.entity.CartProduct;
import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import com.example.tp2_api_rest.ecommerceapi.entity.User;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.exceptions.RessourceExists;
import com.example.tp2_api_rest.ecommerceapi.repository.CartProductRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.CartRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartProductRepository cartProductRepository;


    public Cart addProductToCart(Integer productId, Integer quantity) throws NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();
        Cart cartRe=cartRepository.findByUser(currentUser.getUser_id());
        Integer cartId=cartRe.getCartId();
        // Vérifier si le panier existe, sinon créer un nouveau panier
        Cart cart = cartRepository.findById(cartRe.getCartId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setTotalPrice(0.0);  // initialiser le total à 0
            newCart.setUser(currentUser); // Associate the cart with the current user

            return cartRepository.save(newCart);  // sauvegarder et retourner le nouveau panier
        });

        // Trouver le produit par ID
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produit non trouvé avec ID : " + productId));

        // Vérifier si le produit est déjà dans le panier
        CartProduct existingCartItem = cartProductRepository.findCartItemByProductIdAndCartId(cartId, productId);

        // Si le produit existe déjà dans le panier
        if (existingCartItem != null) {
            // Vérifier la disponibilité en stock
            if (product.getStock() == 0) {
                throw new NotFoundException("Le produit " + product.getName() + " est en rupture de stock.");
            }

            // Vérifier si la quantité commandée est disponible
            if (product.getStock() < quantity) {
                throw new NotFoundException("Quantité commandée de " + product.getName() +
                        " supérieure à la quantité disponible (" + product.getStock() + ").");
            }

            // Mettre à jour la quantité et le prix total
            existingCartItem.setQuantity(existingCartItem.getQuantity() + quantity);
            cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * quantity));

            // Mettre à jour la quantité en stock du produit
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            // Enregistrer les modifications du produit dans le panier
            cartProductRepository.save(existingCartItem);

        } else {
            // Ajouter le produit au panier
            if (product.getStock() == 0) {
                throw new NotFoundException("Le produit " + product.getName() + " est en rupture de stock.");
            }

            // Vérifier si la quantité commandée est disponible
            if (product.getStock() < quantity) {
                throw new NotFoundException("Quantité commandée de " + product.getName() +
                        " supérieure à la quantité disponible (" + product.getStock() + ").");
            }

            // Créer un nouvel élément de panier
            CartProduct newCartItem = new CartProduct();
            newCartItem.setProduct(product);
            newCartItem.setCart(cart);
            newCartItem.setQuantity(quantity);
            newCartItem.setProductPrice(product.getPrice());

            // Sauvegarder le nouvel élément dans le panier
            cartProductRepository.save(newCartItem);

            // Mettre à jour la quantité en stock du produit
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            // Mettre à jour le prix total du panier
            cart.setTotalPrice(cart.getTotalPrice() + (product.getPrice() * quantity));
        }

        // Sauvegarder le panier mis à jour
        return cartRepository.save(cart);
    }

    public Cart getCart() throws NotFoundException {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User currentUser = (User) authentication.getPrincipal();

        Cart cartRe=cartRepository.findByUser(currentUser.getUser_id());
        Integer cartId=cartRe.getCartId();


        // Vérifier si le panier existe
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));

        // Check if the cart belongs to the authenticated user
        if (!cart.getUser().getUser_id().equals(currentUser.getUser_id())) {
            throw new NotFoundException("Access denied: This cart does not belong to you.");
        }
        //inclure des informations sur les produits
        List<CartProduct> cartItems = cart.getCartItems();
        cartItems.forEach(cartItem -> {
            Product product = cartItem.getProduct();
        });

        return cart;
    }



    //delete the cart
    public void deleteCart(Integer cartId) throws NotFoundException {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));

        cartRepository.delete(cart);
    }


    //delete a product from cart

    @Transactional
    public ResponseEntity<String> deleteProductFromCart(Integer productId,Integer quantityToRemove) throws NotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Cart cartRe=cartRepository.findByUser(currentUser.getUser_id());
        Integer cartId=cartRe.getCartId();
        // Vérifie si le panier existe
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));

        // Vérifie si le produit est dans le panier
        CartProduct cartProduct = cartProductRepository.findCartItemByProductIdAndCartId(cartId, productId);

        if (cartProduct == null) {
            throw new NotFoundException("Product not found with ID: " + productId);
        }

        // Vérifie si la quantité à retirer est valide
        if (quantityToRemove <= 0) {
            return ResponseEntity.badRequest().body("Quantity to remove must be greater than zero.");
        }
        if (quantityToRemove > cartProduct.getQuantity()) {
            return ResponseEntity.badRequest().body("Cannot remove more than the available quantity in the cart.");
        }

        // Met à jour le prix total du panier
        cart.setTotalPrice(cart.getTotalPrice() - (cartProduct.getProductPrice() * quantityToRemove));

        // Met à jour la quantité du produit dans le panier
        int newQuantity = cartProduct.getQuantity() - quantityToRemove;
        cartProduct.setQuantity(newQuantity);


        // Met à jour la quantité du produit en stock
        Product product = cartProduct.getProduct();
        product.setStock(product.getStock() + quantityToRemove);

        // Si la nouvelle quantité est 0, on supprime l'élément du panier
        if (newQuantity == 0) {
            cart.getCartItems().remove(cartProduct);
            cartProductRepository.delete(cartProduct);
        }

        // Sauvegarde des modifications
        cartRepository.save(cart);
        cartProductRepository.save(cartProduct);

        return ResponseEntity.ok("Removed " + quantityToRemove + " of product " + cartProduct.getProduct().getName() + " from the cart!");
    }

}
