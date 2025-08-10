package com.hexvoid.inv.orch.product.service;

import com.hexvoid.inv.orch.product.entity.Product;

public interface ProductService {
	
	Product save(Product product);
	Product findById(int id);

}
