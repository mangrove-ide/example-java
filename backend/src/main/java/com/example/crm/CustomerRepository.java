package com.example.crm;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerRepository {

    private static final String CUSTOMER_COLUMNS = "id, first_name AS firstName, last_name AS lastName, email, created_at AS createdAt";

    private final JdbcClient jdbcClient;

    public CustomerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<Customer> list() {
        return jdbcClient.sql("SELECT " + CUSTOMER_COLUMNS + " FROM customers ORDER BY last_name, first_name, id")
                .query(Customer.class)
                .list();
    }

    public Optional<Customer> get(long customerId) {
        return jdbcClient.sql("SELECT " + CUSTOMER_COLUMNS + " FROM customers WHERE id = ?")
                .param(customerId)
                .query(Customer.class)
                .optional();
    }

    public boolean hasEmail(String email) {
        return jdbcClient.sql("SELECT id FROM customers WHERE email = ?")
                .param(email)
                .query(Long.class)
                .optional()
                .isPresent();
    }

    public Customer create(CustomerInput customer) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("INSERT INTO customers (first_name, last_name, email) VALUES (?, ?, ?)")
                .params(customer.firstName(), customer.lastName(), customer.email())
                .update(keyHolder);
        return get(keyHolder.getKey().longValue()).orElseThrow();
    }

    public boolean remove(long customerId) {
        return jdbcClient.sql("DELETE FROM customers WHERE id = ?").param(customerId).update() > 0;
    }

    public void removeAll() {
        jdbcClient.sql("DELETE FROM customers").update();
    }
}
