package com.vojtechruzicka.javafxweaverexample.service;

import com.vojtechruzicka.javafxweaverexample.model.Book;
import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.repositoriy.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final EmployeeRepo employeeRepo;

    @Autowired
    public EmployeeService(EmployeeRepo employeeRepo) {
        this.employeeRepo = employeeRepo;
    }

    @Transactional
    public void addEmployee(Employee employee) {
        if (isEmployeeAlreadyExists(employee)) {
            throw new IllegalArgumentException("Employee with the same values already exists.");

        }
        employeeRepo.save(employee);
    }

    @Transactional
    public void updateEmployee(Employee employee) {
        // Проверяем, что книга существует
//        if (!bookRepo.existsById((long) book.getBookNumber())) {
//            // Можно бросить исключение или выполнить другие действия в случае ошибки
//            throw new IllegalArgumentException("Book does not exist.");
//        }
        employeeRepo.save(employee);
    }

    @Transactional
    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }


    @Transactional
    public void deleteEmployee(int employeeId) {
        employeeRepo.deleteEmployeeByEmployeeId(employeeId);
    }




    private boolean isEmployeeAlreadyExists(Employee newEmployee) {
        List<Employee> existingEmployees = employeeRepo.findAll();

        if(existingEmployees.size() != 1) {
            for (Employee existingEmployee : existingEmployees) {
                if (areEmployeesEqual(existingEmployee, newEmployee)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean areEmployeesEqual(Employee empl1, Employee empl2) {
        // Сравнивайте каждое поле ваших объектов Book
        return empl1.getLastName().equals(empl2.getLastName())
                && empl1.getFirstName().equals(empl2.getFirstName())
                && empl1.getMiddleName().equals(empl2.getMiddleName())
                && empl1.getBirthDate().equals(empl2.getBirthDate())
                && empl1.getPosition().equals(empl2.getPosition())
                && empl1.getEducation().equals(empl2.getEducation());
    }

    public List<Integer> getAllEmployeeIds() {
        return employeeRepo.findAll().stream()
                .map(Employee::getEmployeeId)
                .collect(Collectors.toList());
    }

    public Optional<Employee> getEmployeesByEmployeeId(int employeeId) {
        return employeeRepo.findEmployeeByEmployeeId(employeeId);
    }

}
