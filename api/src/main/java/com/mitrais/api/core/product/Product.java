package com.mitrais.api.core.product;

public class Product {
    private int productId;
    private String name;
    private String serviceAddress;

    public Product() {
        productId = 0;
        name = null;
        serviceAddress = null;
    }

    public Product(int productId, String name, String serviceAddress) {
        this.productId = productId;
        this.name = name;
        this.serviceAddress = serviceAddress;
    }

    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
