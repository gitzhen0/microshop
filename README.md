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