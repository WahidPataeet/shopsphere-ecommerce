# ShopSphere API Gateway

The API Gateway is the single entry point for client requests
to ShopSphere backend services.

## Port

8080

## Routes

| Route | Target Service |
|---|---|
| /api/v1/auth/** | Auth Service :8085 |
| /api/v1/products/** | Product Service :8081 |

## Health Check

GET /actuator/health

## Current Responsibilities

- Request routing
- CORS
- Gateway-level logging
- Central entry point

## Not Yet Implemented

- JWT validation at gateway
- Service discovery
- Load balancing
- Rate limiting
- Circuit breaker