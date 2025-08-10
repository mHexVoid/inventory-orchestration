package com.hexvoid.inv.orch.product.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexvoid.inv.orch.product.dto.ProductDTO;
import com.hexvoid.inv.orch.product.entity.Product;
import com.hexvoid.inv.orch.product.mapper.ProductMapper;
import com.hexvoid.inv.orch.product.service.ProductService;


@RestController
@RequestMapping("/api")
public class ProductController {

	private final ProductService productService;


	public ProductController(ProductService productService ){
		this.productService = productService;
	}


	//	POST /api/products — Create a new product (ADMIN only).
	@PostMapping("/products")
	ResponseEntity<Product> createProducts(@RequestBody ProductDTO productDto){
		Product productDetails = ProductMapper.toEntity(productDto);
		return new ResponseEntity<>(productService.save(productDetails),HttpStatus.CREATED);
	}



	//	GET /api/products/{id} — Returns product info with latest forecast data.
	@GetMapping("/products/{id}")
	ResponseEntity<Product> getProducts(@PathVariable int id){

		return new ResponseEntity<>(productService.findById(id),HttpStatus.OK);
	}



}
