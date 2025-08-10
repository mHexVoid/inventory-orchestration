package com.hexvoid.inv.orch.product.dto;

public class ProductDTO {

	private String name;
	private String category;
	private int current_stock;
	private int reorder_threshold;
	private int lead_time_days;

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
