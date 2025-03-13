package assignment_java5.java5.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import assignment_java5.java5.entitys.Cart;
import assignment_java5.java5.entitys.User;

@Repository
public interface CartDAO extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
