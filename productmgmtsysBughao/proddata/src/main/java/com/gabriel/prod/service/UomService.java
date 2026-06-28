package com.gabriel.prod.service;

import com.gabriel.prod.model.Uom;

public interface UomService {
    Uom[] getUoms() throws Exception;

    Uom getUom(Integer id) throws Exception;

    Uom create(Uom uom) throws Exception;

    Uom update(Uom uom) throws Exception;

    void delete(Integer id) throws Exception;
}
