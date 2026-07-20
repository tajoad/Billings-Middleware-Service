package com.billings.middlewareservice.services.implementation;

import com.billings.middlewareservice.dtos.requests.InvoiceLineRequestDto;
import com.billings.middlewareservice.dtos.requests.InvoiceRequestDto;
import com.billings.middlewareservice.dtos.response.InvoiceLineResponseDto;
import com.billings.middlewareservice.dtos.response.InvoicePaymentHistoryDto;
import com.billings.middlewareservice.dtos.response.InvoiceResponseDto;
import com.billings.middlewareservice.entities.Customer;
import com.billings.middlewareservice.entities.Invoice;
import com.billings.middlewareservice.entities.InvoiceLine;
import com.billings.middlewareservice.entities.Item;
import com.billings.middlewareservice.enums.InvoiceStatus;
import com.billings.middlewareservice.exceptions.ResourceNotFoundException;
import com.billings.middlewareservice.repositories.CustomerRepository;
import com.billings.middlewareservice.repositories.InvoiceRepository;
import com.billings.middlewareservice.repositories.ItemRepository;
import com.billings.middlewareservice.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ItemRepository itemRepository;
    // Assuming you have a CustomerRepository
    private final CustomerRepository customerRepository;
    @Override
    public InvoiceResponseDto createInvoice(InvoiceRequestDto requestDto) {
        Customer customer = customerRepository.findById(requestDto.customerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + requestDto.customerId()));

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(generateNextInvoiceNumber());
        invoice.setCustomer(customer);
        invoice.setStatus(InvoiceStatus.DRAFT);

        BigDecimal totalGross = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;

        for (InvoiceLineRequestDto lineReq : requestDto.lines()) {
            Item item = itemRepository.findById(lineReq.itemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Item not found with ID: " + lineReq.itemId()));

            BigDecimal quantity = BigDecimal.valueOf(lineReq.quantity());
            BigDecimal baseSubTotal = item.getUnitPrice().multiply(quantity);

            // Tax parsing logic: e.g., 7.5% -> 0.075 fraction conversion
            BigDecimal taxFraction = item.getTaxRate().divide(BigDecimal.valueOf(100));
            BigDecimal computedLineTax = baseSubTotal.multiply(taxFraction);
            BigDecimal computedLineNet = baseSubTotal.add(computedLineTax);

            InvoiceLine line = new InvoiceLine();
            line.setItem(item); // Directly binds the rich item entity proxy relationship
            line.setQuantity(quantity);
            line.setUnitPrice(item.getUnitPrice());
            line.setLineTaxAmount(computedLineTax);
            line.setLineNetAmount(computedLineNet);

            // Establish full bidirectional references cleanly using your helper
            invoice.addLine(line);

            // Roll totals into master entity aggregates
            totalGross = totalGross.add(baseSubTotal);
            totalTax = totalTax.add(computedLineTax);
        }

        invoice.setGrossAmount(totalGross);
        invoice.setTaxAmount(totalTax);
        invoice.setNetAmount(totalGross.add(totalTax));

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToResponseDto(savedInvoice);
    }

    @Override
    public InvoiceResponseDto getInvoiceById(UUID id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found with ID: " + id));
        return convertToResponseDto(invoice);
    }

    @Override
    public List<InvoiceResponseDto> getAllInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .toList();
    }

    private synchronized String generateNextInvoiceNumber() {
        return invoiceRepository.findLastInvoiceNumber()
                .map(lastNum -> {
                    try {
                        String numericPart = lastNum.substring(4);
                        long nextNum = Long.parseLong(numericPart) + 1;
                        return String.format("INV-%05d", nextNum);
                    } catch (Exception e) {
                        return "INV-00001";
                    }
                })
                .orElse("INV-00001");
    }

    private InvoiceResponseDto convertToResponseDto(Invoice invoice) {
        List<InvoiceLineResponseDto> lineDtos = invoice.getLines().stream()
                .map(line -> {
                    // Pull item metadata transparently out of the child lazy relationship context
                    Item linkedItem = line.getItem();
                    BigDecimal baseSubTotal = line.getUnitPrice().multiply(line.getQuantity());

                    return new InvoiceLineResponseDto(
                            line.getId(),
                            linkedItem.getId(),
                            linkedItem.getItemCode(),
                            linkedItem.getName(),
                            line.getQuantity().intValue(), // Convert BigDecimal down to standard Integer for DTO
                            line.getUnitPrice(),
                            linkedItem.getTaxRate(),
                            baseSubTotal,
                            line.getLineTaxAmount(),
                            line.getLineNetAmount()
                    );
                }).toList();

        List<InvoicePaymentHistoryDto> history = invoice.getPayments().stream()
                .map(p -> new InvoicePaymentHistoryDto(
                        p.getId(),
                        p.getPaymentReference(),
                        p.getAmountPaid(),
                        p.getPaymentMethod(),
                        p.getProcessedAt() // Ensure your entity's processedAt is an Instant now too!
                ))
                .toList();

        return new InvoiceResponseDto(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getCustomer().getId(),
                invoice.getCreatedAt(),
                lineDtos,
                invoice.getGrossAmount(),
                invoice.getTaxAmount(),
                invoice.getNetAmount(),
                history
        );
    }
}
