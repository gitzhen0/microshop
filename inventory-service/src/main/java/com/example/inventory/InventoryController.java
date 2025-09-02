package com.example.inventory;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/inventory", produces = "application/json")
public class InventoryController {
    private final InventoryRepository repo;

    public InventoryController(InventoryRepository repo) { this.repo = repo; }

    // 列表：GET /inventory  -> [{sku, stock}, ...]
    @GetMapping
    public List<Map<String, Object>> list() {
        var out = new ArrayList<Map<String, Object>>();
        for (Inventory i : repo.findAll()) {
            out.add(Map.of("sku", i.getSku(), "stock", i.getStock()));
        }
        return out;
    }

    // 查询：GET /inventory/{sku}  -> {sku, stock}
    @GetMapping("/{sku}")
    public ResponseEntity<Map<String,Object>> get(@PathVariable String sku){
        return repo.findBySku(sku)
                .map(i -> {
                    Map<String,Object> out = new HashMap<>();
                    out.put("sku", i.getSku());
                    out.put("stock", i.getStock());
                    return ResponseEntity.ok(out);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 调整：POST /inventory/{sku}  body: {"delta": 5}  -> {sku, stock}
    @PostMapping(path = "/{sku}", consumes = "application/json")
    @Transactional
    public Map<String,Object> add(@PathVariable String sku, @RequestBody Update u){
        int delta = Optional.ofNullable(u.delta()).orElse(0);
        var inv = repo.findBySku(sku).orElseGet(() -> new Inventory(sku, 0));
        inv.setStock(inv.getStock() + delta);
        repo.save(inv);
        return Map.of("sku", inv.getSku(), "stock", inv.getStock());
    }

    public record Update(@NotNull Integer delta) {}
}