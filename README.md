# microshop

### Intro
Demo project for NebulaOps

* docker 开 db
```shell
docker run -d --name microshop-postgres \
  -e POSTGRES_USER=shop -e POSTGRES_PASSWORD=shop123 -e POSTGRES_DB=shopdb \
  -p 5432:5432 postgres:16
```

* 启动顺序
  * users (8081) → orders (8082) → inventory (8083) → api-gateway (8080)

### 测试
```shell
# 用户
curl -s -X POST http://localhost:8080/api/users -H 'Content-Type: application/json' -d '{"name":"Alice"}' | jq
curl -s http://localhost:8080/api/users | jq

# 库存
curl -s http://localhost:8080/api/inventory/SKU-1 | jq
curl -s -X POST http://localhost:8080/api/inventory/SKU-1 -H 'Content-Type: application/json' -d '{"delta": 5}' | jq

# 订单
curl -s -X POST http://localhost:8080/api/orders -H 'Content-Type: application/json' -d '{"sku":"SKU-1","qty":2}' | jq
curl -s http://localhost:8080/api/orders | jq
```

### API
	•	Users
	•	✅ Create：POST /api/users
	•	✅ Read：GET /api/users、GET /api/users/{id}
	•	⛔ Update：暂无（可加 PUT/PATCH /api/users/{id}）
	•	⛔ Delete：暂无（可加 DELETE /api/users/{id}）
	•	Orders
	•	✅ Create：POST /api/orders
	•	✅ Read：GET /api/orders、GET /api/orders/{id}
	•	⛔ Update：暂无（通常订单不允许任意更新；可加“取消”动作）
	•	⛔ Delete：暂无（一般不物理删，改状态）
	•	Inventory
	•	✅ Create：隐式（对不存在的 SKU 调 POST /api/inventory/{sku} 会创建）
	•	✅ Read：GET /api/inventory、GET /api/inventory/{sku}
	•	✅ Update：POST /api/inventory/{sku}（{"delta": n} 增量调整）
	•	⛔ Delete：暂无（可加 DELETE /api/inventory/{sku}）

### trigger cicd off
