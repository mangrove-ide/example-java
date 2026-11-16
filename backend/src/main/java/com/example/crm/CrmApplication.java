package com.example.crm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrmApplication {

    public static void main(String[] args) throws IOException {
        createDatabaseDirectory(System.getenv().getOrDefault("DATABASE_PATH", "data/crm.sqlite"));
        SpringApplication.run(CrmApplication.class, args);
    }

    /** SQLite creates the file but not its parent directory. */
    static void createDatabaseDirectory(String databasePath) throws IOException {
        Path parent = Path.of(databasePath).toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
    }
}
