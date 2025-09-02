package com.example.orders;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/orders") // 只写路径，不要在类级别写 method/consumes/produces
public class OrderController {
    private final OrderRepository repo;
    public OrderController(OrderRepository repo) { this.repo = repo; }

    public record CreateOrder(@NotBlank String sku, @Min(1) int qty) {}

    @GetMapping
    public List<Order> list() { return repo.findAll(); }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public Order create(@RequestBody CreateOrder req) { return repo.save(new Order(req.sku(), req.qty())); }

    @GetMapping("/{id}")
    public ResponseEntity<Order> get(@PathVariable Long id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}