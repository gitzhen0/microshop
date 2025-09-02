package com.example.orders;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private int qty;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected Order() {}
    public Order(String sku, int qty) { this.sku = sku; this.qty = qty; }

    public Long getId() { return id; }
    public String getSku() { return sku; }
    public int getQty() { return qty; }
    public Instant getCreatedAt() { return createdAt; }
}