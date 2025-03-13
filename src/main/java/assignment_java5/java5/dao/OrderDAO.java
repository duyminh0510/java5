package assignment_java5.java5.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import assignment_java5.java5.entitys.Order;
import assignment_java5.java5.entitys.User;

@Repository
public interface OrderDAO extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    List<Order> findByUserOrderByCreatedAtDesc(User user);

    List<Order> findByStatus(String status);

    List<Order> findByUserAndStatus(User user, String status);

}
