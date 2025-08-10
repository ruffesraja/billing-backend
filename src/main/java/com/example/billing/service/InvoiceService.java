package com.example.billing.service;

import com.example.billing.dto.invoice.CreateInvoiceRequestDto;
import com.example.billing.dto.invoice.InvoiceItemDto;
import com.example.billing.dto.invoice.InvoiceResponseDto;
import com.example.billing.dto.invoice.UpdateInvoiceRequestDto;
import com.example.billing.entity.Invoice;
import com.example.billing.entity.Product;
import com.example.billing.enums.InvoiceStatus;
import com.example.billing.mapper.InvoiceMapper;
import com.example.billing.repository.InvoiceRepository;
import com.example.billing.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class InvoiceService {
    
    private final InvoiceRepository invoiceRepository;
    private final ProductRepository productRepository;
    private final InvoiceMapper invoiceMapper;
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getAllInvoices() {
        log.debug("Fetching all invoices");
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getInvoicesWithFilters(InvoiceStatus status, LocalDate startDate, LocalDate endDate, String dateFilter) {
        log.debug("Fetching invoices with filters: status={}, startDate={}, endDate={}, dateFilter={}", 
                status, startDate, endDate, dateFilter);
        
        // Calculate date range based on dateFilter if provided
        LocalDate filterStartDate = startDate;
        LocalDate filterEndDate = endDate;
        
        if (dateFilter != null && !dateFilter.isEmpty()) {
            LocalDate today = LocalDate.now();
            switch (dateFilter.toLowerCase()) {
                case "today":
                    filterStartDate = today;
                    filterEndDate = today;
                    break;
                case "yesterday":
                    filterStartDate = today.minusDays(1);
                    filterEndDate = today.minusDays(1);
                    break;
                case "this_week":
                    filterStartDate = today.with(DayOfWeek.MONDAY);
                    filterEndDate = today.with(DayOfWeek.SUNDAY);
                    break;
                case "last_week":
                    filterStartDate = today.minusWeeks(1).with(DayOfWeek.MONDAY);
                    filterEndDate = today.minusWeeks(1).with(DayOfWeek.SUNDAY);
                    break;
                case "this_month":
                    filterStartDate = today.withDayOfMonth(1);
                    filterEndDate = today.withDayOfMonth(today.lengthOfMonth());
                    break;
                case "last_month":
                    LocalDate lastMonth = today.minusMonths(1);
                    filterStartDate = lastMonth.withDayOfMonth(1);
                    filterEndDate = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth());
                    break;
                case "this_quarter":
                    int currentQuarter = (today.getMonthValue() - 1) / 3;
                    filterStartDate = today.withMonth(currentQuarter * 3 + 1).withDayOfMonth(1);
                    filterEndDate = filterStartDate.plusMonths(2).withDayOfMonth(filterStartDate.plusMonths(2).lengthOfMonth());
                    break;
                case "last_quarter":
                    int lastQuarter = (today.getMonthValue() - 1) / 3 - 1;
                    if (lastQuarter < 0) {
                        lastQuarter = 3;
                        filterStartDate = today.minusYears(1).withMonth(lastQuarter * 3 + 1).withDayOfMonth(1);
                    } else {
                        filterStartDate = today.withMonth(lastQuarter * 3 + 1).withDayOfMonth(1);
                    }
                    filterEndDate = filterStartDate.plusMonths(2).withDayOfMonth(filterStartDate.plusMonths(2).lengthOfMonth());
                    break;
                case "this_year":
                    filterStartDate = today.withDayOfYear(1);
                    filterEndDate = today.withDayOfYear(today.lengthOfYear());
                    break;
                case "last_year":
                    filterStartDate = today.minusYears(1).withDayOfYear(1);
                    filterEndDate = today.minusYears(1).withDayOfYear(today.minusYears(1).lengthOfYear());
                    break;
                case "last_30_days":
                    filterStartDate = today.minusDays(30);
                    filterEndDate = today;
                    break;
                case "last_90_days":
                    filterStartDate = today.minusDays(90);
                    filterEndDate = today;
                    break;
            }
        }
        
        List<Invoice> invoices;
        
        // Apply filters based on available parameters
        if (status != null && filterStartDate != null && filterEndDate != null) {
            invoices = invoiceRepository.findByStatusAndInvoiceDateBetween(status, filterStartDate, filterEndDate);
        } else if (status != null) {
            invoices = invoiceRepository.findByStatus(status);
        } else if (filterStartDate != null && filterEndDate != null) {
            invoices = invoiceRepository.findByInvoiceDateBetween(filterStartDate, filterEndDate);
        } else {
            invoices = invoiceRepository.findAll();
        }
        
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    @Transactional(readOnly = true)
    public InvoiceResponseDto getInvoiceById(Long id) {
        log.debug("Fetching invoice with id: {}", id);
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        return invoiceMapper.toResponseDto(invoice);
    }
    
    public InvoiceResponseDto createInvoice(CreateInvoiceRequestDto requestDto) {
        log.debug("Creating invoice for customer: {}", requestDto.getCustomerName());
        
        // Create line items with dynamic pricing from request
        List<Invoice.InvoiceLineItem> lineItems = new ArrayList<>();
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        
        for (int i = 0; i < requestDto.getItems().size(); i++) {
            InvoiceItemDto itemDto = requestDto.getItems().get(i);
            
            // Determine if this is a custom product
            boolean isCustomProduct = Boolean.TRUE.equals(itemDto.getIsCustomProduct()) || itemDto.getProductId() == null;
            
            if (!isCustomProduct && itemDto.getProductId() != null) {
                // Regular product - verify it exists
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDto.getProductId()));
                log.debug("Using existing product: {} (ID: {})", product.getName(), product.getId());
            } else {
                // Custom product - no validation needed
                log.debug("Using custom product: {}", itemDto.getProductName());
            }
            
            // Calculate line total (no tax at line level)
            BigDecimal unitPrice = itemDto.getUnitPrice();
            BigDecimal quantity = BigDecimal.valueOf(itemDto.getQuantity());
            BigDecimal lineTotal = unitPrice.multiply(quantity);
            
            Invoice.InvoiceLineItem lineItem = Invoice.InvoiceLineItem.builder()
                    .serialNumber(itemDto.getSerialNumber() != null ? itemDto.getSerialNumber() : i + 1)
                    .productId(isCustomProduct ? null : itemDto.getProductId())
                    .productName(itemDto.getProductName())
                    .description(itemDto.getDescription())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineTotal)
                    .isCustomProduct(isCustomProduct)
                    .build();
            
            lineItems.add(lineItem);
            subtotalAmount = subtotalAmount.add(lineTotal);
        }
        
        // Calculate GST if applicable
        BigDecimal cgstAmount = BigDecimal.ZERO;
        BigDecimal sgstAmount = BigDecimal.ZERO;
        BigDecimal totalGstAmount = BigDecimal.ZERO;
        
        if (Boolean.TRUE.equals(requestDto.getGstApplicable())) {
            if (requestDto.getCgstRate() != null) {
                cgstAmount = subtotalAmount.multiply(requestDto.getCgstRate())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
            if (requestDto.getSgstRate() != null) {
                sgstAmount = subtotalAmount.multiply(requestDto.getSgstRate())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }
            totalGstAmount = cgstAmount.add(sgstAmount);
        }
        
        // Calculate additional charges
        BigDecimal transportCharges = requestDto.getTransportCharges() != null ? requestDto.getTransportCharges() : BigDecimal.ZERO;
        BigDecimal miscCharges = requestDto.getMiscCharges() != null ? requestDto.getMiscCharges() : BigDecimal.ZERO;
        
        // Calculate total amount
        BigDecimal totalAmount = subtotalAmount.add(totalGstAmount).add(transportCharges).add(miscCharges);
        
        // Create invoice with customer details from request and JSON line items
        Invoice invoice = Invoice.builder()
                .invoiceNumber(generateInvoiceNumber())
                .customerName(requestDto.getCustomerName())
                .customerEmail(requestDto.getCustomerEmail())
                .customerPhone(requestDto.getCustomerPhone())
                .customerAddress(requestDto.getCustomerAddress())
                .customerGstNumber(requestDto.getCustomerGstNumber())
                .invoiceDate(requestDto.getInvoiceDate())
                .dueDate(requestDto.getDueDate())
                .status(InvoiceStatus.UNPAID)
                .notes(requestDto.getNotes())
                .subtotalAmount(subtotalAmount)
                .gstApplicable(Boolean.TRUE.equals(requestDto.getGstApplicable()))
                .cgstRate(requestDto.getCgstRate())
                .sgstRate(requestDto.getSgstRate())
                .cgstAmount(cgstAmount)
                .sgstAmount(sgstAmount)
                .totalGstAmount(totalGstAmount)
                .transportChargesLabel(requestDto.getTransportChargesLabel())
                .transportCharges(transportCharges)
                .miscChargesLabel(requestDto.getMiscChargesLabel())
                .miscCharges(miscCharges)
                .totalAmount(totalAmount)
                .lineItems(lineItems)
                .build();
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.debug("Created invoice with id: {} and number: {}", savedInvoice.getId(), savedInvoice.getInvoiceNumber());
        
        return invoiceMapper.toResponseDto(savedInvoice);
    }
    
    public InvoiceResponseDto updateInvoice(Long id, UpdateInvoiceRequestDto requestDto) {
        log.debug("Updating invoice with id: {}", id);
        
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + id));
        
        // Update customer details
        invoice.setCustomerName(requestDto.getCustomerName());
        invoice.setCustomerEmail(requestDto.getCustomerEmail());
        invoice.setCustomerPhone(requestDto.getCustomerPhone());
        invoice.setCustomerAddress(requestDto.getCustomerAddress());
        invoice.setCustomerGstNumber(requestDto.getCustomerGstNumber());
        
        // Update invoice details
        invoice.setInvoiceDate(requestDto.getInvoiceDate());
        invoice.setDueDate(requestDto.getDueDate());
        invoice.setStatus(requestDto.getStatus());
        invoice.setNotes(requestDto.getNotes());
        
        // Update GST settings
        invoice.setGstApplicable(requestDto.getGstApplicable() != null ? requestDto.getGstApplicable() : false);
        invoice.setCgstRate(requestDto.getCgstRate());
        invoice.setSgstRate(requestDto.getSgstRate());
        
        // Update additional charges
        invoice.setTransportChargesLabel(requestDto.getTransportChargesLabel());
        invoice.setTransportCharges(requestDto.getTransportCharges());
        invoice.setMiscChargesLabel(requestDto.getMiscChargesLabel());
        invoice.setMiscCharges(requestDto.getMiscCharges());
        
        // Update line items
        List<Invoice.InvoiceLineItem> lineItems = new ArrayList<>();
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        
        for (int i = 0; i < requestDto.getItems().size(); i++) {
            InvoiceItemDto itemDto = requestDto.getItems().get(i);
            
            // Validate product if not custom
            if (!itemDto.getIsCustomProduct() && itemDto.getProductId() != null) {
                Product product = productRepository.findById(itemDto.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemDto.getProductId()));
                
                // Calculate line total
                BigDecimal unitPrice = itemDto.getUnitPrice();
                BigDecimal quantity = BigDecimal.valueOf(itemDto.getQuantity());
                BigDecimal lineTotal = unitPrice.multiply(quantity);
                
                Invoice.InvoiceLineItem lineItem = Invoice.InvoiceLineItem.builder()
                        .serialNumber(i + 1)
                        .productId(product.getId())
                        .productName(product.getName())
                        .description(itemDto.getDescription() != null ? itemDto.getDescription() : product.getName())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .lineTotal(lineTotal)
                        .isCustomProduct(false)
                        .build();
                
                lineItems.add(lineItem);
                subtotalAmount = subtotalAmount.add(lineTotal);
            } else {
                // Custom product
                BigDecimal unitPrice = itemDto.getUnitPrice();
                BigDecimal quantity = BigDecimal.valueOf(itemDto.getQuantity());
                BigDecimal lineTotal = unitPrice.multiply(quantity);
                
                Invoice.InvoiceLineItem lineItem = Invoice.InvoiceLineItem.builder()
                        .serialNumber(i + 1)
                        .productId(null)
                        .productName(itemDto.getProductName())
                        .description(itemDto.getDescription())
                        .quantity(itemDto.getQuantity())
                        .unitPrice(itemDto.getUnitPrice())
                        .lineTotal(lineTotal)
                        .isCustomProduct(true)
                        .build();
                
                lineItems.add(lineItem);
                subtotalAmount = subtotalAmount.add(lineTotal);
            }
        }
        
        invoice.setLineItems(lineItems);
        invoice.setSubtotalAmount(subtotalAmount);
        
        // Recalculate GST if applicable
        BigDecimal cgstAmount = BigDecimal.ZERO;
        BigDecimal sgstAmount = BigDecimal.ZERO;
        BigDecimal totalGstAmount = BigDecimal.ZERO;
        
        if (invoice.getGstApplicable() && invoice.getCgstRate() != null && invoice.getSgstRate() != null) {
            // Calculate CGST and SGST on subtotal
            cgstAmount = subtotalAmount.multiply(invoice.getCgstRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            sgstAmount = subtotalAmount.multiply(invoice.getSgstRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            totalGstAmount = cgstAmount.add(sgstAmount);
        }
        
        invoice.setCgstAmount(cgstAmount);
        invoice.setSgstAmount(sgstAmount);
        invoice.setTotalGstAmount(totalGstAmount);
        
        // Calculate additional charges
        BigDecimal transportCharges = invoice.getTransportCharges() != null ? invoice.getTransportCharges() : BigDecimal.ZERO;
        BigDecimal miscCharges = invoice.getMiscCharges() != null ? invoice.getMiscCharges() : BigDecimal.ZERO;
        
        // Calculate total amount
        BigDecimal totalAmount = subtotalAmount.add(totalGstAmount).add(transportCharges).add(miscCharges);
        invoice.setTotalAmount(totalAmount);
        
        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.debug("Updated invoice with id: {}", savedInvoice.getId());
        
        return invoiceMapper.toResponseDto(savedInvoice);
    }
    
    public void deleteInvoice(Long id) {
        log.debug("Deleting invoice with id: {}", id);
        
        if (!invoiceRepository.existsById(id)) {
            throw new RuntimeException("Invoice not found with id: " + id);
        }
        
        invoiceRepository.deleteById(id);
        log.debug("Deleted invoice with id: {}", id);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getInvoicesByStatus(InvoiceStatus status) {
        log.debug("Fetching invoices with status: {}", status);
        List<Invoice> invoices = invoiceRepository.findByStatus(status);
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> searchInvoicesByCustomerName(String customerName) {
        log.debug("Searching invoices for customer name: {}", customerName);
        List<Invoice> invoices = invoiceRepository.findByCustomerNameContainingIgnoreCase(customerName);
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    @Transactional(readOnly = true)
    public List<InvoiceResponseDto> getInvoicesByCustomerEmail(String customerEmail) {
        log.debug("Fetching invoices for customer email: {}", customerEmail);
        List<Invoice> invoices = invoiceRepository.findByCustomerEmail(customerEmail);
        return invoiceMapper.toResponseDtoList(invoices);
    }
    
    private String generateInvoiceNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "INV-" + timestamp;
    }
}
