package com.hexvoid.inv.orch.product.service;

import org.springframework.stereotype.Service;

import com.hexvoid.inv.orch.product.entity.Product;
import com.hexvoid.inv.orch.product.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService{
	
	private ProductRepository productRepository;
	
	ProductServiceImpl(ProductRepository productRepository){
		this.productRepository=productRepository;
	}
	

	@Override
	public Product save(Product product) {
		
		return productRepository.save(product);
	}

	@Override
	public Product findById(int id) {
		
		return productRepository.findById(id).orElseThrow();
	}

}
