package com.example.crm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public List<Customer> list() {
        return customerRepository.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@RequestBody(required = false) CustomerInput body) {
        CustomerInput customer = parseCustomer(body);
        if (customerRepository.hasEmail(customer.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "A customer with this email already exists");
        }
        return customerRepository.create(customer);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String customerId) {
        if (!isInteger(customerId) || !customerRepository.remove(Long.parseLong(customerId))) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Customer not found");
        }
    }

    private static CustomerInput parseCustomer(CustomerInput body) {
        CustomerInput input = body == null ? new CustomerInput(null, null, null) : body;
        Map<String, String> values = new LinkedHashMap<>();
        values.put("firstName", trim(input.firstName()));
        values.put("lastName", trim(input.lastName()));
        values.put("email", trim(input.email()));
        List<String> missingFields = values.entrySet().stream().filter(entry -> entry.getValue().isEmpty()).map(Map.Entry::getKey).toList();
        if (!missingFields.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Missing required fields: " + String.join(", ", missingFields));
        }
        if (!EMAIL_PATTERN.matcher(values.get("email")).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email is not valid");
        }
        return new CustomerInput(values.get("firstName"), values.get("lastName"), values.get("email"));
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean isInteger(String value) {
        return value.matches("-?\\d{1,18}");
    }
}
