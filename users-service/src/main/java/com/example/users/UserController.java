package com.example.users;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {

    // 按插入顺序返回
    private final Map<String, UserDto> store =
            Collections.synchronizedMap(new LinkedHashMap<>());

    // 列表：GET /users
    @GetMapping
    public List<UserDto> list() {
        return new ArrayList<>(store.values());
    }

    // 按ID：GET /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable String id) {
        var u = store.get(id);
        return (u == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(u);
    }

    // 新增：POST /users
    @PostMapping(consumes = "application/json", produces = "application/json")
    public UserDto create(@RequestBody CreateUser req) {
        var id = UUID.randomUUID().toString();
        var u = new UserDto(id, req.name());
        store.put(id, u);
        return u;
    }

    public record CreateUser(@NotBlank String name) {}
}

// 也可以保持为顶级 record（与你原来一致）
record UserDto(String id, String name) {}