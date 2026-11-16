package com.example.crm;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/** Inserts demo customers. Run with --crm.seed=true --spring.main.web-application-type=none so the app exits afterwards (see README). */
@Component
@ConditionalOnProperty(name = "crm.seed", havingValue = "true")
public class DemoSeeder implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemoSeeder.class);

    private static final List<CustomerInput> DEMO_CUSTOMERS = List.of(
            new CustomerInput("Ada", "Lovelace", "ada@example.com"),
            new CustomerInput("Grace", "Hopper", "grace@example.com"),
            new CustomerInput("Alan", "Turing", "alan@example.com"),
            new CustomerInput("Katherine", "Johnson", "katherine@example.com"));

    private final CustomerRepository customerRepository;
    private final Environment environment;

    public DemoSeeder(CustomerRepository customerRepository, Environment environment) {
        this.customerRepository = customerRepository;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
        List<CustomerInput> newCustomers = DEMO_CUSTOMERS.stream().filter(customer -> !customerRepository.hasEmail(customer.email())).toList();
        newCustomers.forEach(customerRepository::create);
        LOGGER.info("Seeded {} customers into {}", newCustomers.size(), environment.getProperty("crm.database-path"));
    }
}
