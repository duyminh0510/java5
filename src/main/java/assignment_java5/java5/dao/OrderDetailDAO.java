package assignment_java5.java5.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import assignment_java5.java5.entitys.OrderDetail;

@Repository
public interface OrderDetailDAO extends JpaRepository<OrderDetail, Long> {
}
