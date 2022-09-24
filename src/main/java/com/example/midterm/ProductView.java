package com.example.midterm;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Route("")
public class ProductView extends VerticalLayout {
    private ComboBox<Product> productList;
    private TextField productName;
    private NumberField productCost, productProfit, productPrice;
    private Button addbtn,updatebtn, delbtn, clearbtn;

    public ProductView(){
        productList = new ComboBox<Product>("Product List");
        productList.getStyle().set("width", "600px");

        productName = new TextField("Product Name");
        productName.getStyle().set("width", "600px");

        productCost = new NumberField("Product Cost");
        productCost.getStyle().set("width", "600px");

        productProfit = new NumberField("Product Profit");
        productProfit.getStyle().set("width", "600px");

        productPrice = new NumberField("Product price");
        productPrice.getStyle().set("width", "600px");
        productPrice.setEnabled(false);

        addbtn = new Button("Add Product");
        updatebtn = new Button("Update Product");
        delbtn = new Button("Delete Product");
        clearbtn = new Button("Clear Product");

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(addbtn, updatebtn, delbtn, clearbtn);
        add(productList, productName, productCost, productProfit, productPrice, horizontalLayout);

        productList.addFocusListener(e -> {
            this.fetchProduct();
        });
        productList.addValueChangeListener(e -> {
            if (this.productList.getValue() != null) {
                this.productName.setValue(this.productList.getValue().getProductName());
                this.productCost.setValue(this.productList.getValue().getProductCost());
                this.productProfit.setValue(this.productList.getValue().getProductProfit());
                this.productPrice.setValue(this.productList.getValue().getProductPrice());
            } else {
                this.clearProduct();
            }
        });
        productCost.addKeyPressListener(e -> {
            if (e.getKey().equals("Enter")) {
                this.CalculatorPrice();
            }
        });
        productProfit.addKeyPressListener(e -> {
            if (e.getKey().equals("Enter")) {
                this.CalculatorPrice();
            }
        });

        addbtn.addClickListener(e -> {
            this.CalculatorPrice();
            String name = productName.getValue();
            double cost = productCost.getValue();
            double profit = productProfit.getValue();
            double price = productPrice.getValue();

            WebClient
                    .create()
                    .post()
                    .uri("http://localhost:8080/addProduct")
                    .body(Mono.just(new Product(null, name, cost, profit, price)), Product.class)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            this.fetchProduct();
        });

        updatebtn.addClickListener(e -> {
            this.CalculatorPrice();
            String id = productList.getValue().get_id();
            String name = productName.getValue();
            double cost = productCost.getValue();
            double profit = productProfit.getValue();
            double price = productPrice.getValue();

            WebClient
                    .create()
                    .post()
                    .uri("http://localhost:8080/updateProduct")
                    .body(Mono.just(new Product(id, name, cost, profit, price)), Product.class)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            this.fetchProduct();
        });

        delbtn.addClickListener(e -> {
            String id = productList.getValue().get_id();
            String name = productName.getValue();
            double cost = productCost.getValue();
            double profit = productProfit.getValue();
            double price = productPrice.getValue();

            WebClient
                    .create()
                    .post()
                    .uri("http://localhost:8080/deleteProduct")
                    .body(Mono.just(new Product(id, name, cost, profit, price)), Product.class)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            this.fetchProduct();
            this.clearProduct();
        });

        clearbtn.addClickListener(e -> {
            this.clearProduct();
            new Notification("Cleared", 500).open();
        });

    }
    public void fetchProduct(){
        List<Product> products = WebClient
                .create()
                .get()
                .uri("http://localhost:8080/getProducts")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Product>>() {})
                .block();
        this.productList.setItems(products);
    }
    public void CalculatorPrice(){
        double cost = productCost.getValue();
        double profit = productProfit.getValue();

        Double price = WebClient
                .create()
                .get()
                .uri("http://localhost:8080/getPrice/" + cost + "/" + profit)
                .retrieve()
                .bodyToMono(Double.class)
                .block();
        this.productPrice.setValue(price);
    }

    private ComponentEventListener clearProduct() {
        productName.setValue("");
        productCost.setValue(0.0);
        productProfit.setValue(0.0);
        productPrice.setValue(0.0);
        this.fetchProduct();
        return null;
    }
}
