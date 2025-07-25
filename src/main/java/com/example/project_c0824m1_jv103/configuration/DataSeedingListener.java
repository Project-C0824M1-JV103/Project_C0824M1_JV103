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

        if (employeeRepository.findByRole(Employee.Role.Admin).isEmpty()) {
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
        }

//        if (employeeRepository.findByRole(Employee.Role.Sales).isEmpty()) {
            if (employeeRepository.findByEmail("viettai@gmail.com") == null) {
                Employee sales = new Employee(
                        "Viết Tài",
                        "viettai@gmail.com",
                        EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                        "0123456790",
                        Employee.Role.Sales,
                        Employee.Status.active
                );
                employeeRepository.save(sales);
            }
//        }

//        if (employeeRepository.findByRole(Employee.Role.Sales).isEmpty()) {
            if (employeeRepository.findByEmail("congdat@gmail.com") == null) {
                Employee sales = new Employee(
                        "Công Đạt",
                        "congdat@gmail.com",
                        EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                        "0123456810",
                        Employee.Role.Sales,
                        Employee.Status.active
                );
                employeeRepository.save(sales);
            }
//        }

//        if (employeeRepository.findByRole(Employee.Role.Warehouse).isEmpty()) {
            if (employeeRepository.findByEmail("vantai@gmail.com") == null) {
                Employee warehouse = new Employee(
                        "Văn Tài",
                        "vantai@gmail.com",
                        EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                        "0123456791",
                        Employee.Role.Warehouse,
                        Employee.Status.active
                );
                employeeRepository.save(warehouse);
            }
//        }

//        if (employeeRepository.findByRole(Employee.Role.Warehouse).isEmpty()) {
            if (employeeRepository.findByEmail("vanhien@gmail.com") == null) {
                Employee warehouse = new Employee(
                        "Văn Hiển",
                        "vanhien@gmail.com",
                        EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                        "0123456811",
                        Employee.Role.Warehouse,
                        Employee.Status.active
                );
                employeeRepository.save(warehouse);
            }
//        }

//        if (employeeRepository.findByRole(Employee.Role.Business).isEmpty()) {
            if (employeeRepository.findByEmail("hongquan@gmail.com") == null) {
                Employee business = new Employee(
                        "Hồng Quân",
                        "hongquan@gmail.com",
                        EncryptPasswordUtils.EncryptPasswordUtils("123456"),
                        "0123456792",
                        Employee.Role.Business,
                        Employee.Status.active
                );
                employeeRepository.save(business);
            }
//        }
    }

    @Override
    public boolean supportsAsyncExecution() {
        return ApplicationListener.super.supportsAsyncExecution();
    }
}