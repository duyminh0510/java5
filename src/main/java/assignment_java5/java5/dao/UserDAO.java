package assignment_java5.java5.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import assignment_java5.java5.entitys.User;

@Repository
public interface UserDAO extends JpaRepository<User, String> {
    User findByPhoneOrEmail(String phone, String email);

    List<User> findByIsActiveTrue();

    User findByEmail(String email);

    
    User findByUsername(String username);


    @Query("SELECT u.username, SUM(o.totalAmount), MIN(o.createdAt), MAX(o.createdAt) " +
            "FROM Order o " +
            "JOIN o.user u " +
            "GROUP BY u.username " +
            "ORDER BY SUM(o.totalAmount) DESC " +
            "LIMIT 10")
    List<Object[]> getTop10VipCustomers();

    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmailPass(@Param("email") String email);

    Optional<User> findByResetToken(String resetToken);
}
