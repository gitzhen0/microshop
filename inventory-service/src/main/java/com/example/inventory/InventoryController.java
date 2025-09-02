package com.example.inventory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/inventory")
public class InventoryController {
    private final Map<String,Integer> stock = Collections.synchronizedMap(new LinkedHashMap<>());
    public InventoryController() {
        stock.put("SKU-1", 100); stock.put("SKU-2", 50);
    }
    @GetMapping("/{sku}") public ResponseEntity<Map<String,Object>> get(@PathVariable String sku){
        Integer s = stock.get(sku);
        return s==null ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(Map.of("sku", sku, "stock", s));
    }
    record Update(int delta){}
    @PostMapping("/{sku}") public Map<String,Object> add(@PathVariable String sku, @RequestBody Update u){
        stock.put(sku, stock.getOrDefault(sku,0)+u.delta());
        return Map.of("sku", sku, "stock", stock.get(sku));
    }
}