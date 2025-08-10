package com.hexvoid.inv.orch.product.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hexvoid.inv.orch.product.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

}
