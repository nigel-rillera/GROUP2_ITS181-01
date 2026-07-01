package com.gabriel.prod.model;

import lombok.Data;

@Data
public class    Product {
    int id;
    String name;
    String description;

    int uomId;

    String uomName;

    String photo;

    @Override
    public String toString(){
        return name;
    }
}
