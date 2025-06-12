package com.example.project_c0824m1_jv103.configuration;


import com.example.project_c0824m1_jv103.common.EncryptPasswordUtils;
import com.example.project_c0824m1_jv103.model.Employee;
import com.example.project_c0824m1_jv103.repository.AccountRepository;
import com.example.project_c0824m1_jv103.repository.IEmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeedingListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IEmployeeRepository employeeRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (employeeRepository.findByEmail("admin@example.com") == null) {
            Employee admin = new Employee(
                    "Admin Name",
                    "admin@example.com",
                    EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                    "0123456789",
                    Employee.Role.Admin,
                    Employee.Status.active
            );
            employeeRepository.save(admin);
        }

        if (employeeRepository.findByEmail("sales@example.com") == null) {
            Employee sales = new Employee(
                    "Sales User",
                    "sales@example.com",
                    EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                    "0123456790",
                    Employee.Role.Sales,
                    Employee.Status.active
            );
            employeeRepository.save(sales);
        }

        if (employeeRepository.findByEmail("warehouse@example.com") == null) {
            Employee warehouse = new Employee(
                    "Warehouse Staff",
                    "warehouse@example.com",
                    EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                    "0123456791",
                    Employee.Role.Warehouse,
                    Employee.Status.active
            );
            employeeRepository.save(warehouse);
        }

        if (employeeRepository.findByEmail("business@example.com") == null) {
            Employee business = new Employee(
                    "Business Analyst",
                    "business@example.com",
                    EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                    "0123456792",
                    Employee.Role.Business,
                    Employee.Status.active
            );
            employeeRepository.save(business);
        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}