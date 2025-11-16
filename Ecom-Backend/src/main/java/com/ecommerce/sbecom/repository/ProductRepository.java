package com.ecommerce.sbecom.repository;

import com.ecommerce.sbecom.model.Category;
import com.ecommerce.sbecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {


    Page<Product> findByProductNameLikeIgnoreCase(String keyword, Pageable pageDetails);

    Page<Product> findByCategory(Category category, Pageable pageDetails);
}
