package com.example.tp2_api_rest.ecommerceapi.controller;


import com.example.tp2_api_rest.ecommerceapi.entity.Cart;
import com.example.tp2_api_rest.ecommerceapi.entity.CartProduct;
import com.example.tp2_api_rest.ecommerceapi.entity.Product;
import com.example.tp2_api_rest.ecommerceapi.exceptions.NotFoundException;
import com.example.tp2_api_rest.ecommerceapi.exceptions.RessourceExists;
import com.example.tp2_api_rest.ecommerceapi.repository.CartProductRepository;
import com.example.tp2_api_rest.ecommerceapi.repository.CartRepository;
import com.example.tp2_api_rest.ecommerceapi.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @PostMapping("/addProduct")
    public ResponseEntity<Cart> addProductToCart(
            @RequestParam Integer productId,
            @RequestParam Integer quantity) {
        try {
            // Appel du service pour ajouter un produit au panier
            Cart updatedCart = cartService.addProductToCart( productId, quantity);
            return new ResponseEntity<>(updatedCart, HttpStatus.OK);
        } catch (NotFoundException e) {
            // Gestion des erreurs si le panier ou le produit n'est pas trouvé
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (RessourceExists e) {
            // Gestion des erreurs personnalisées (produit déjà dans le panier, stock insuffisant)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/getMyCart")
    public ResponseEntity<Cart> getCart() {
        try {
            Cart cart = cartService.getCart();
            return new ResponseEntity<>(cart, HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("delete/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Integer cartId) {
        try {
            cartService.deleteCart(cartId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        } catch (NotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404 Not Found
        }
    }

    @DeleteMapping("/removeProduct/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @PathVariable Integer productId) {
        try {
            // Appeler la méthode de service qui retourne un ResponseEntity
            return cartService.deleteProductFromCart(productId); // Renvoie directement le ResponseEntity du service
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred."); // 500 Internal Server Error
        }
    }




}
