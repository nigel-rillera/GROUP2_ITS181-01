package com.gabriel.prod.service;

import com.gabriel.prod.model.Product;

public interface ProductService {
    Product[] getProducts() throws Exception;

    Product getProduct(Integer id) throws Exception;

    Product create(Product product) throws Exception;

    Product update(Product product) throws Exception;

    void delete(Integer id) throws Exception;
}
