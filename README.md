# Billing Application - Spring Boot Backend

A complete Spring Boot backend application for managing billing operations with customers, products, and invoices.

## Features

- **Customer Management**: Create, read, update, and delete customers
- **Product Management**: Manage products with pricing and tax information
- **Invoice Management**: Create invoices with line items and automatic calculations
- **DTO-based Architecture**: Clean separation between API and entity layers
- **PostgreSQL Database**: Robust data persistence
- **Validation**: Input validation with proper error handling
- **Logging**: Comprehensive logging for debugging and monitoring

## Technology Stack

- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **PostgreSQL**
- **MapStruct** for DTO mapping
- **Lombok** for reducing boilerplate code
- **Maven** for dependency management

## Database Setup

1. Install PostgreSQL
2. Create a database named `billing_db`
3. Update the database credentials in `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/billing_db
    username: your_username
    password: your_password
```

## Running the Application

1. Clone the repository
2. Navigate to the project directory
3. Run the application:

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Customer APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/customers` | Get all customers |
| GET | `/api/customers/{id}` | Get customer by ID |
| POST | `/api/customers` | Create new customer |
| PUT | `/api/customers/{id}` | Update customer |
| DELETE | `/api/customers/{id}` | Delete customer |

#### Create Customer Request:
```json
{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0123",
  "address": "123 Main St, Anytown, USA"
}
```

### Product APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products` | Get all products |
| GET | `/api/products?search=laptop` | Search products by name |
| GET | `/api/products/{id}` | Get product by ID |
| POST | `/api/products` | Create new product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

#### Create Product Request:
```json
{
  "name": "Laptop",
  "description": "High-performance laptop",
  "unitPrice": 999.99,
  "taxPercent": 8.25
}
```

### Invoice APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/invoices` | Get all invoices |
| GET | `/api/invoices?customerId=1` | Get invoices by customer |
| GET | `/api/invoices?status=UNPAID` | Get invoices by status |
| GET | `/api/invoices/{id}` | Get invoice by ID with items |
| POST | `/api/invoices` | Create new invoice |
| PUT | `/api/invoices/{id}` | Update invoice |
| DELETE | `/api/invoices/{id}` | Delete invoice |

#### Create Invoice Request:
```json
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

## Invoice Status Enum

- `PAID`: Invoice has been paid
- `UNPAID`: Invoice is pending payment
- `OVERDUE`: Invoice is past due date

## Sample Data

The application automatically loads sample data on startup:
- 2 sample customers
- 3 sample products (Laptop, Wireless Mouse, Office Chair)

## Error Handling

The application includes comprehensive error handling:
- Validation errors return detailed field-level messages
- Business logic errors return appropriate error responses
- All errors are logged for debugging

## Project Structure

```
src/main/java/com/billing/
├── controller/          # REST controllers
├── service/            # Business logic layer
├── repository/         # Data access layer
├── entity/            # JPA entities
├── dto/               # Data transfer objects
├── mapper/            # MapStruct mappers
├── enums/             # Enumerations
├── exception/         # Exception handling
└── config/            # Configuration classes
```

## Testing the APIs

You can test the APIs using tools like Postman, curl, or any REST client. Here are some example curl commands:

### Create a Customer:
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

### Create an Invoice:
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

## Features Implemented

✅ Complete CRUD operations for all entities
✅ DTO-based architecture with MapStruct
✅ Automatic invoice number generation
✅ Tax and total calculations
✅ Comprehensive validation
✅ Error handling with detailed messages
✅ Logging throughout the application
✅ Sample data loading
✅ PostgreSQL integration
✅ Transactional service methods

## Future Enhancements

- Add pagination for list endpoints
- Implement invoice PDF generation
- Add email notifications
- Implement payment tracking
- Add audit logging
- Implement soft delete functionality
- Add API documentation with Swagger/OpenAPI
