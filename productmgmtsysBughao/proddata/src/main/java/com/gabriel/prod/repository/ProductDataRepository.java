package com.gabriel.prod.repository;

import com.gabriel.prod.entity.ProductData;
import org.springframework.data.repository.CrudRepository;

public interface ProductDataRepository extends CrudRepository<ProductData,Integer> {}