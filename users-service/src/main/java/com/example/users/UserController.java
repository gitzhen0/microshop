package com.example.users;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<String, UserDto> store = Collections.synchronizedMap(new LinkedHashMap<>());

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> get(@PathVariable String id) {
        var u = store.get(id);
        return u == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(u);
    }

    @PostMapping
    public UserDto create(@RequestBody CreateUser req) {
        var id = UUID.randomUUID().toString();
        var u = new UserDto(id, req.name());
        store.put(id, u);
        return u;
    }

    public record CreateUser(@NotBlank String name) {}
}
record UserDto(String id, String name) {}