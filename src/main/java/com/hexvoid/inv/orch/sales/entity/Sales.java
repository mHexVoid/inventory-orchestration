package com.hexvoid.inv.orch.sales.entity;

import java.time.LocalDate;

import com.hexvoid.inv.orch.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="sales")
public class Sales {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name="quantity_sold")
	private int quantitySold;

	@Column(name="sales_date")
	private LocalDate salesdate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantitySold() {
		return quantitySold;
	}

	public void setQuantitySold(int quantitySold) {
		this.quantitySold = quantitySold;
	}

	public LocalDate getSalesdate() {
		return salesdate;
	}

	public void setSalesdate(LocalDate salesdate) {
		this.salesdate = salesdate;
	}
}
