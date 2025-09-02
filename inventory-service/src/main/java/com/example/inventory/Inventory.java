package com.example.inventory;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory", indexes = @Index(name = "uk_inventory_sku", columnList = "sku", unique = true))
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private int stock;

    protected Inventory() {}
    public Inventory(String sku, int stock) { this.sku = sku; this.stock = stock; }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
}