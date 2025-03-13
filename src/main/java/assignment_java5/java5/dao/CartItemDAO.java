package assignment_java5.java5.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import assignment_java5.java5.entitys.Cart;
import assignment_java5.java5.entitys.CartItem;
import assignment_java5.java5.entitys.Product;
import assignment_java5.java5.entitys.User;

@Repository
public interface CartItemDAO extends JpaRepository<CartItem, Long> {
    // Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    Optional<CartItem> findByCartAndProductAndSize(Cart cart, Product product, String size);

    List<CartItem> findByCart(Cart cart);

    List<CartItem> findByCartUser(User user);

    void deleteByCart(Cart cart);

}
