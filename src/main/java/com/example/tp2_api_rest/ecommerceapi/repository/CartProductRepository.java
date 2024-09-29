package com.example.tp2_api_rest.ecommerceapi.repository;

import com.example.tp2_api_rest.ecommerceapi.entity.Cart;
import com.example.tp2_api_rest.ecommerceapi.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Integer> {

    @Query("SELECT cp FROM CartProduct cp WHERE cp.cart.cartId= :cartId AND cp.product.product_id = :productId")
    CartProduct findCartItemByProductIdAndCartId(@Param("cartId") Integer cartId, @Param("productId") Integer productId);
}

