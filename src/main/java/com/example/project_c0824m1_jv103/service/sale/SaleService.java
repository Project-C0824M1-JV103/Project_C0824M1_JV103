package com.example.project_c0824m1_jv103.service.sale;

import com.example.project_c0824m1_jv103.model.Sale;
import com.example.project_c0824m1_jv103.repository.ISaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaleService implements ISaleService {

    @Autowired
    private ISaleRepository saleRepository;

    @Override
    public Sale createSale(Sale sale) {
        return saleRepository.save(sale);
    }
}