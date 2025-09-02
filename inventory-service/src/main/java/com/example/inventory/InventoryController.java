package com.example.inventory;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryRepository repo;

    public InventoryController(InventoryRepository repo) { this.repo = repo; }

    record Update(int delta) {}

//    @GetMapping("/{sku}")
//    public ResponseEntity<Map<String,Object>> get(@PathVariable String sku){
//        return repo.findBySku(sku)
//                .map(i -> ResponseEntity.ok(Map.of("sku", i.getSku(), "stock", i.getStock())))
//                .orElse(ResponseEntity.notFound().build());
//    }
    @GetMapping("/{sku}")
    public ResponseEntity<Inventory> get(@PathVariable String sku){
        return repo.findBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{sku}")
    @Transactional
    public Map<String,Object> add(@PathVariable String sku, @RequestBody Update u){
        var inv = repo.findBySku(sku).orElseGet(() -> new Inventory(sku, 0));
        inv.setStock(inv.getStock() + u.delta());
        repo.save(inv);
        return Map.of("sku", inv.getSku(), "stock", inv.getStock());
    }
}