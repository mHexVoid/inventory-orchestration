package com.hexvoid.inv.orch.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;

	@Column(name="name")
	private String name;

	@Column(name="category")
	private String category;

	@Column(name="current_stock")
	private int current_stock;

	@Column(name="reorder_threshold")
	private int reorder_threshold;

	@Column(name="lead_time_days")
	private int lead_time_days;


	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", category=" + category + ", current_stock=" + current_stock
				+ ", reorder_threshold=" + reorder_threshold + ", lead_time_days=" + lead_time_days + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public int getCurrent_stock() {
		return current_stock;
	}

	public void setCurrent_stock(int current_stock) {
		this.current_stock = current_stock;
	}

	public int getReorder_threshold() {
		return reorder_threshold;
	}

	public void setReorder_threshold(int reorder_threshold) {
		this.reorder_threshold = reorder_threshold;
	}

	public int getLead_time_days() {
		return lead_time_days;
	}

	public void setLead_time_days(int lead_time_days) {
		this.lead_time_days = lead_time_days;
	}

}
