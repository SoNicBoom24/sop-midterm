package com.example.midterm;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {
    @Autowired
    private ProductService service;

    @PostMapping("/addProduct")
    @RabbitListener(queues = "AddProductQueue")
    public boolean serviceAddProduct(@RequestBody Product product) {
        return service.addProduct(product);
    }

    @PostMapping("/updateProduct")
    @RabbitListener(queues = "UpdateProductQueue")
    public boolean serviceUpdateProduct(@RequestBody Product product) {
        return service.updateProduct(product);
    }

    @PostMapping("/deleteProduct")
    @RabbitListener(queues = "DeleteProductQueue")
    public boolean serviceDeleteProduct(@RequestBody Product product){
        return service.deleteProduct(product);
    }

    @GetMapping("/getProductName/{name}")
    @RabbitListener(queues = "GetNameProductQueue")
    public Product serviceGetProductName(@PathVariable ("name") String name){
        return service.getProductByName(name);
    }

    @GetMapping("/getProducts")
    @RabbitListener(queues = "GetAllProductQueue")
    public List<Product> serviceGetAllProduct(){
        return service.getAllProduct();
    }
}
