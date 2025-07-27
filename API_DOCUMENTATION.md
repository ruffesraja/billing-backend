# 🧾 Billing Application API Documentation

## 🚀 Overview
Complete Spring Boot REST API for billing management with customers, products, and invoices.

**Base URL**: `http://localhost:8080`

---

## 📋 API Endpoints

### 👥 Customer Management

#### Get All Customers
```http
GET /api/customers
```

**Response:**
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "address": "123 Main St, Anytown, USA",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

#### Get Customer by ID
```http
GET /api/customers/{id}
```

#### Create Customer
```http
POST /api/customers
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "address": "123 Main St, Anytown, USA"
}
```

#### Update Customer
```http
PUT /api/customers/{id}
Content-Type: application/json

{
  "name": "John Updated",
  "email": "john.updated@example.com",
  "phone": "+1-555-9999",
  "address": "456 New St, Updated City, USA"
}
```

#### Delete Customer
```http
DELETE /api/customers/{id}
```

---

### 📦 Product Management

#### Get All Products
```http
GET /api/products
```

#### Search Products
```http
GET /api/products?search=laptop
```

#### Get Product by ID
```http
GET /api/products/{id}
```

#### Create Product
```http
POST /api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "High-performance laptop for business use",
  "unitPrice": 999.99,
  "taxPercent": 8.25
}
```

#### Update Product
```http
PUT /api/products/{id}
Content-Type: application/json

{
  "name": "Updated Laptop",
  "description": "Updated description",
  "unitPrice": 1199.99,
  "taxPercent": 10.0
}
```

#### Delete Product
```http
DELETE /api/products/{id}
```

---

### 🧾 Invoice Management

#### Get All Invoices
```http
GET /api/invoices
```

#### Get Invoices by Customer
```http
GET /api/invoices?customerId=1
```

#### Get Invoices by Status
```http
GET /api/invoices?status=UNPAID
```

**Available Status Values:**
- `PAID`
- `UNPAID`
- `OVERDUE`

#### Get Invoice by ID (with items)
```http
GET /api/invoices/{id}
```

**Response:**
```json
{
  "id": 1,
  "invoiceNumber": "INV-20240115103000",
  "customer": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0123",
    "address": "123 Main St, Anytown, USA",
    "createdAt": "2024-01-15T10:30:00"
  },
  "invoiceDate": "2024-01-15",
  "dueDate": "2024-02-15",
  "status": "UNPAID",
  "totalAmount": 2159.97,
  "notes": "Monthly service invoice",
  "createdAt": "2024-01-15T10:30:00",
  "invoiceItems": [
    {
      "id": 1,
      "product": {
        "id": 1,
        "name": "Laptop",
        "description": "High-performance laptop",
        "unitPrice": 999.99,
        "taxPercent": 8.25,
        "createdAt": "2024-01-15T10:00:00"
      },
      "quantity": 2,
      "unitPrice": 999.99,
      "taxPercent": 8.25,
      "total": 2164.98
    }
  ]
}
```

#### Create Invoice
```http
POST /api/invoices
Content-Type: application/json

{
  "customerId": 1,
  "invoiceDate": "2024-01-15",
  "dueDate": "2024-02-15",
  "notes": "Monthly service invoice",
  "items": [
    {
      "productId": 1,
      "quantity": 2
    },
    {
      "productId": 2,
      "quantity": 1
    }
  ]
}
```

#### Update Invoice
```http
PUT /api/invoices/{id}
Content-Type: application/json

{
  "invoiceDate": "2024-01-20",
  "dueDate": "2024-02-20",
  "status": "PAID",
  "notes": "Updated invoice notes"
}
```

#### Delete Invoice
```http
DELETE /api/invoices/{id}
```

---

## 🧪 Testing with cURL

### Create a Customer
```bash
curl -X POST http://localhost:8080/api/customers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Customer",
    "email": "test@example.com",
    "phone": "+1-555-9999",
    "address": "Test Address"
  }'
```

### Create a Product
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Test Description",
    "unitPrice": 99.99,
    "taxPercent": 8.25
  }'
```

### Create an Invoice
```bash
curl -X POST http://localhost:8080/api/invoices \
  -H "Content-Type: application/json" \
  -d '{
    "customerId": 1,
    "invoiceDate": "2024-01-15",
    "dueDate": "2024-02-15",
    "notes": "Test invoice",
    "items": [
      {
        "productId": 1,
        "quantity": 1
      }
    ]
  }'
```

### Get All Customers
```bash
curl -X GET http://localhost:8080/api/customers
```

### Get Invoice with Items
```bash
curl -X GET http://localhost:8080/api/invoices/1
```

---

## ⚠️ Error Responses

### Validation Error (400 Bad Request)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input parameters",
  "validationErrors": {
    "name": "Name is required",
    "email": "Email should be valid"
  }
}
```

### Not Found Error (400 Bad Request)
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Customer not found with id: 999"
}
```

---

## 🔧 Configuration

### Database Setup
1. Install PostgreSQL
2. Create database: `CREATE DATABASE billing;`
3. Update `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/billing
    username: your_username
    password: your_password
```

### Running the Application
```bash
# Using Maven wrapper
./mvnw spring-boot:run

# Or using Maven
mvn spring-boot:run
```

---

## 📊 Sample Data

The application automatically loads sample data on startup:

**Customers:**
- John Doe (john.doe@example.com)
- Jane Smith (jane.smith@example.com)

**Products:**
- Laptop ($999.99, 8.25% tax)
- Wireless Mouse ($29.99, 8.25% tax)
- Office Chair ($199.99, 8.25% tax)

---

## 🎯 Key Features

✅ **Complete CRUD Operations** for all entities  
✅ **DTO-based Architecture** with MapStruct mapping  
✅ **Automatic Invoice Number Generation** (INV-timestamp format)  
✅ **Tax and Total Calculations** automatically computed  
✅ **Comprehensive Validation** with detailed error messages  
✅ **Global Exception Handling** with structured responses  
✅ **Transactional Service Methods** for data consistency  
✅ **Sample Data Loading** for immediate testing  
✅ **Search Functionality** for products  
✅ **Filtering Support** for invoices by customer/status  

---

## 🚀 Next Steps

1. **Set up PostgreSQL** database
2. **Update database credentials** in application.yml
3. **Run the application**: `./mvnw spring-boot:run`
4. **Test the APIs** using the provided cURL commands
5. **Integrate with frontend** application

Your complete Spring Boot billing application is ready to use! 🎉
