# Atta Chakki Backend (Spring Boot)

A secure, modular, and scalable Spring Boot backend built to digitalize and automate daily operations of an Atta Chakki (Flour Mill) business.
This backend manages customers, orders, payments, dues/refunds, staff permissions, shop operations, and personalization settings.
---

## 🎯 Project Goals

- Build a real-world, domain-driven backend for a small-scale business
- Apply clean architecture and modular design
- Implement secure JWT-based authentication and permission based access
- Follow industry-level API design and validation
- Prepare the backend for future mobile & web clients
- Pagination, sorting, and filtering
---

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.x**
- **Spring Security + JWT**
- **Spring Data JPA**
- **MySQL Database**
- **MapStruct (DTO Mapping)**
- **Maven**
- **Validation (Jakarta Validation)**
---

## 🏗️ Project Structure

    atta-chakki-backend/
    │─ src/main/java/com/attachakki
    │  │─ components/
    │  │  ├─ customer/         # Customer management
    │  │  ├─ order/            # Orders module
    │  │  ├─ payment/          # Payments, dues, refunds
    │  │  ├─ staff/            # Shop staff & roles
    │  │  ├─ shop/             # Shop operations
    │  │  ├─ customerLedger    # Due & refund logic
    │  │  └─ shop-operations/  # shop-operations tracking
    │  │
    │  ├─ controllers/        # Globle controllers
    │  ├─ security/           # JWT auth, filters, user details
    │  ├─ exception/          # Custom exceptions
    │  ├─ services/           # Globle services
    │  └─ config/             # Security + App configurations
    │── src/main/resources/             
    │   ├── application.yml   # Database & app configs
    │── pom.xml               # Maven Dependencies
---

## 🔑 Security Model

The application uses role/permission-based authorization.

Example dynamic authority:
```txt 
SHOP_1_CUSTOMER_FULL
SHOP_1_ORDER_FULL
SHOP_1_PAYMENT_READ
SHOP_1_STAFF_FULL
```

This allows permissions to be scoped to a particular shop instead of giving a user unrestricted access to every shop.

---

## 🔐 API Overview

### **Authentication**
- `POST /auth/login` — Login and receive Access + Refresh tokens
- `POST /auth/register` — Register shop admin/staff
- `GET /auth/refresh-token` — Refresh JWT access token

### **User Details**

- `GET /v1/users` - Fetch paginated list of users (admin-level).
- `GET /v1/users/me` - Fetch currently authenticated user details.
- `PATCH /v1/users/me` - Update currently authenticated user details (partial update).
- `DELETE /v1/users/me` - Delete currently authenticated user account.

### **Shop Management**
- `GET /v1/shops/u/{userDetailsId}` — List all shops for a user
- `GET /v1/shops/{shopId}` — Get shop details by ID
- `POST /v1/shops` — Create a new shop
- `PATCH /v1/shops/{shopId}/status` — Update only shop status
- `PATCH /v1/shops/{shopId}` — Update shop fields (partial update)
- `DELETE /v1/shops/{shopId}` — Delete shop by ID

### **Address Management**
- `GET /address` — List all addresses (supports pagination & sorting)
- `GET /address/{id}` — Get address by ID
- `POST /address` — Add a new address
- `PATCH /address/{id}` — Update address (partial update)
- `DELETE /address/{id}` — Delete address by ID

### **Shop Staff Management**
- `GET /v1/shops/{shopId}/staffs` — List all shop staff (supports pagination & sorting)
- `GET /v1/shops/{shopId}/staffs/{shopStaffId}` — Get shop staff details by ID
- `POST /v1/shops/{shopId}/staffs` — Register/create a new shop staff
- `PATCH /v1/shops/{shopId}/staffs/{shopStaffId}` — Update shop staff (partial update)
- `DELETE /v1/shops/{shopId}/staffs/{shopStaffId}` — Delete shop staff by ID

### **Staff Permission Management**
- `GET /v1/shops/{shopId}/permissions/staffs/{staffId}` — List permissions for a staff (supports pagination & sorting)
- `POST /v1/shops/{shopId}/permissions/staffs/{staffId}` — Create/grant permissions for a staff
- `DELETE /v1/shops/{shopId}/permissions/{permissionId}` — Delete a permission by ID

### **Address Management**
- `GET /address` — List all addresses (supports pagination & sorting)
- `GET /address/{id}` — Get address by ID
- `POST /address` — Add a new address
- `PATCH /address/{id}` — Update address (partial update)
- `DELETE /address/{id}` — Delete address by ID

### **Order Management**
- `GET /v1/shops/{shopId}/orders/customers/{customerId}` — List all orders for a customer (supports pagination & sorting)
- `GET /v1/shops/{shopId}/orders/total` — Get total outstanding debt for the shop
- `POST /v1/shops/{shopId}/orders/customers/{customerId}` — Create a new order for a customer
- `PATCH /v1/shops/{shopId}/orders/{orderId}` — Update order fields (partial update)
- `DELETE /v1/shops/{shopId}/orders/{orderId}` — Delete order by ID
- `GET /v1/shops/{shopId}/orders/customers/{customerId}/export?type=CSV` — Export customer orders as CSV

### **Order Item Management**
- `GET /v1/order-items` — List all order items (supports pagination & sorting)
- `POST /v1/order-items` — Create a new order item
- `PATCH /v1/order-items/{orderItemId}` — Update order item (partial update)
- `DELETE /v1/order-items/{orderItemId}` — Delete order item by ID

### **Shop Order Item Pricing Management**
- `GET /v1/shops/{shopId}/prices` — List all shop order-item prices (supports pagination & sorting)
- `POST /v1/shops/{shopId}/prices` — Create a new shop order-item price
- `PATCH /v1/shops/{shopId}/prices/{orderItemPriceId}` — Update shop order-item price (partial update)
- `DELETE /v1/shops/{shopId}/prices/{orderItemPriceId}` — Delete shop order-item price by ID

### **Payment Management**
- `GET /v1/shops/{shopId}/payments/customers/{customerId}` — List all payments for a customer (supports pagination & sorting)
- `GET /v1/shops/{shopId}/payments/{paymentId}` — Get payment details by ID
- `POST /v1/shops/{shopId}/payments/customers/{customerId}` — Create a new payment (supports bundled orderIds + payment + due/refund)
- `PATCH /v1/shops/{shopId}/payments/{paymentId}` — Update payment details (partial update)
- `DELETE /v1/shops/{shopId}/payments/{paymentId}` — Delete payment record

### **User Personalization**
- `GET /v1/personalizations/users/{userDetailId}` — Fetch personalization for a user
- `PATCH /v1/personalizations/users/{userDetailId}` — Update personalization (partial update)

### **Shop Operations**
- `GET /v1/shops/{shopId}/shop-operations` — Fetch all operations done in shop
- `DELETE /v1/shops/{shopId}/shop-operations/{operationId}` — Delete shop operation

> All protected APIs require: Authorization: Bearer {JwtAccessToken}
---
## 📖 **Configuration Overview (Internal)**

This project is configured for a real-world, single-business deployment.
Environment-specific values such as database credentials, JWT secrets,
and OAuth configurations are managed internally and are not documented
for public setup.

The repository focuses on demonstrating backend architecture,
API design, and business workflows rather than deployment instructions.

---
## 👨‍💻 Author
- **Karan Buradkar**  
  [LinkedIn](https://linkedin.com/in/karanburadkar) | [GitHub](https://github.com/KaranBuradkar)
---

## 🔑 License

This project is licensed under the MIT License.
See the [LICENSE](LICENSE) file for details.