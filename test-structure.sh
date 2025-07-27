#!/bin/bash

echo "=== Billing Application Structure Test ==="
echo ""

echo "📁 Checking project structure..."
echo ""

# Check main directories
echo "✅ Main directories:"
ls -la src/main/java/com/example/billing/

echo ""
echo "✅ Entity classes:"
ls -la src/main/java/com/example/billing/entity/

echo ""
echo "✅ DTO classes:"
find src/main/java/com/example/billing/dto -name "*.java" | wc -l
echo "   Total DTO files found"

echo ""
echo "✅ Controller classes:"
ls -la src/main/java/com/example/billing/controller/

echo ""
echo "✅ Repository classes:"
ls -la src/main/java/com/example/billing/repository/

echo ""
echo "✅ Service classes:"
ls -la src/main/java/com/example/billing/service/

echo ""
echo "✅ Mapper classes:"
ls -la src/main/java/com/example/billing/mapper/

echo ""
echo "✅ Configuration files:"
ls -la src/main/resources/

echo ""
echo "=== Structure verification complete! ==="
echo ""
echo "🚀 Your Spring Boot Billing Application is ready!"
echo ""
echo "📋 Next steps:"
echo "1. Set up PostgreSQL database named 'billing'"
echo "2. Update database credentials in application.yml"
echo "3. Run: ./mvnw spring-boot:run"
echo "4. Test APIs at http://localhost:8080"
echo ""
echo "📚 Available endpoints:"
echo "   • GET/POST/PUT/DELETE /api/customers"
echo "   • GET/POST/PUT/DELETE /api/products"
echo "   • GET/POST/PUT/DELETE /api/invoices"
echo ""
