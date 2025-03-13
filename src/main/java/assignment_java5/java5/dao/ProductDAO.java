package assignment_java5.java5.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import assignment_java5.java5.entitys.Category;
import assignment_java5.java5.entitys.Product;

@Repository
public interface ProductDAO extends JpaRepository<Product, Integer> {
    // Page<Product> findAllByproductNameLike(String keywords, Pageable pageable);
    boolean existsByCategory(Category category);

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") String categoryId, Pageable pageable);

    // List<Product> findByproductNameContainingIgnoreCase(String keyword);
    Page<Product> findByproductNameContainingIgnoreCase(String keyword, Pageable pageable);

    @Query("SELECT c.name AS categoryName, SUM(o.totalAmount) AS totalRevenue, " +
            "SUM(od.quantity) AS totalQuantity, MAX(od.price) AS maxPrice, " +
            "MIN(od.price) AS minPrice, AVG(od.price) AS avgPrice " +
            "FROM OrderDetail od " +
            "JOIN od.product p " +
            "JOIN p.category c " +
            "JOIN od.order o " +
            "GROUP BY c.name")
    List<Object[]> getRevenueByCategory();

}
