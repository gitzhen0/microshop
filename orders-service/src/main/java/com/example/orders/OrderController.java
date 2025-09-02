package com.example.orders;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController @RequestMapping("/orders")
public class OrderController {
    record Order(String id, String sku, int qty){}
    record CreateOrder(@NotBlank String sku, int qty){}
    private final Map<String, Order> store = Collections.synchronizedMap(new LinkedHashMap<>());

    @PostMapping public Order create(@RequestBody CreateOrder req){
        var id = UUID.randomUUID().toString();
        var o = new Order(id, req.sku(), req.qty());
        store.put(id, o);
        return o;
    }
    @GetMapping("/{id}") public ResponseEntity<Order> get(@PathVariable String id){
        var o = store.get(id);
        return o==null?ResponseEntity.notFound().build():ResponseEntity.ok(o);
    }
}