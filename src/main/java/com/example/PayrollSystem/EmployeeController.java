/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.PayrollSystem;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 *
 * @author jeant
 */
@RestController
class EmployeeController {

    private EmployeeRepository repository;

    public EmployeeController(EmployeeRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> readEmployees() {
        List<EntityModel<Employee>> employees = repository.findAll().stream()
                .map(employee -> EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).readEmployee(employee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).readEmployees()).withRel("employees")))
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).readEmployees()).withSelfRel());
    }

    @GetMapping("/employees/{id}")
    EntityModel<Employee> readEmployee(@PathVariable Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        return EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).readEmployee(id)).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).readEmployees()).withRel("employees"));
    }

    @PostMapping("/employees")
    EntityModel<Employee> createEmployee(@RequestBody Employee newEmployee) {
        Employee employee = repository.save(newEmployee);

        return EntityModel.of(employee,
                linkTo(methodOn(EmployeeController.class).readEmployee(employee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).readEmployees()).withRel("employees"));
    }

    @PutMapping("/employees/{id}")
    EntityModel<Employee> updateEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        Employee updatedEmployee = repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> repository.save(newEmployee));

        return EntityModel.of(updatedEmployee,
                linkTo(methodOn(EmployeeController.class).readEmployee(updatedEmployee.getId())).withSelfRel(),
                linkTo(methodOn(EmployeeController.class).readEmployees()).withRel("employees"));
    }

    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }

}
