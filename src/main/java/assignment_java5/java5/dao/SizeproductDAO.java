package assignment_java5.java5.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import assignment_java5.java5.entitys.Sizeproduct;
import jakarta.transaction.Transactional;

@Repository
public interface SizeproductDAO extends JpaRepository<Sizeproduct, Integer> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Sizeproduct sp WHERE sp.product.productId = :productId")
    void deleteByProductId(@Param("productId") Integer productId);
}
