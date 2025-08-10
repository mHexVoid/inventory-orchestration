package com.hexvoid.inv.orch.product.mapper;

import com.hexvoid.inv.orch.product.dto.ProductDTO;
import com.hexvoid.inv.orch.product.entity.Product;

public class ProductMapper {

	private ProductMapper() {

	}

	public static Product toEntity(ProductDTO productDto) {

		Product product = new Product();
		product.setName(productDto.getName());
		product.setCategory(productDto.getCategory());
		product.setCurrent_stock(productDto.getCurrent_stock());
		product.setLead_time_days(productDto.getLead_time_days());
		product.setReorder_threshold(productDto.getReorder_threshold());

		return product;
	}

}
