package assignment_java5.java5.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import assignment_java5.java5.dao.ProductDAO;
import assignment_java5.java5.entitys.Product;

@Service
public class ProductService {

    @Autowired
    private ProductDAO productDAO;

    public Page<Product> searchByName(String keyword, Pageable pageable) {
        return productDAO.findByproductNameContainingIgnoreCase(keyword, pageable);
    }

    // public Product findById(Integer id) {
    // Optional<Product> product = productDAO.findById(id);
    // return product.orElse(null);
    // }

    public Product findById(Integer id) {
        return productDAO.findById(id).orElse(null);
    }

}
