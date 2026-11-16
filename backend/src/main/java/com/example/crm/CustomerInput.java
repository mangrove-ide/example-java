package com.example.crm;

/** Request body for creating a customer; fields are nullable because the client may omit them. */
public record CustomerInput(String firstName, String lastName, String email) {
}
