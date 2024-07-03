package com.laptrinhJava.demo.repository;

import com.laptrinhJava.demo.Model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% OR p.description LIKE %:keyword%")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.name LIKE %:keyword% OR p.description LIKE %:keyword%) AND p.category.id = :categoryId")
    Page<Product> findByKeywordAndCategory(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (p.name LIKE %:keyword% OR p.description LIKE %:keyword%) AND p.category.name LIKE %:categoryName%")
    Page<Product> findByKeywordAndCategoryName(@Param("keyword") String keyword, @Param("categoryName") String categoryName, Pageable pageable);

    // Hàm tìm kiếm các sản phẩm có tên chứa từ khóa, giới hạn top 5
    List<Product> findTop5ByNameContaining(String keyword);

    Page<Product> findByCategoryName(String categoryName, Pageable pageable);

}
