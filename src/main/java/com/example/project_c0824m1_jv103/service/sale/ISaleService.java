package com.example.project_c0824m1_jv103.service.sale;

import com.example.project_c0824m1_jv103.model.Sale;
import java.util.Optional;

public interface ISaleService {
    Sale createSale(Sale sale);
    Optional<Sale> findById(Integer id);
} 