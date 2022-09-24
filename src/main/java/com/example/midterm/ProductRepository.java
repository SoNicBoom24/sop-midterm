package com.example.midterm;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductRepository extends MongoRepository<Product, String> {
    @Query("{ productName: '?0' }")
    Product findByName(String productName);
}
