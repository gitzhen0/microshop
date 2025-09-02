package com.example.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository repo;
    public OrderController(OrderRepository repo) { this.repo = repo; }

    public record CreateOrder(@NotBlank String sku, @Min(1) int qty) {}

    @PostMapping
    public Order create(@RequestBody CreateOrder req) {
        return repo.save(new Order(req.sku(), req.qty()));
    }

    @GetMapping
    public List<Order> list() { return repo.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}