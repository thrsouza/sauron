package com.github.thrsouza.sauron.application.customer;

import java.util.Optional;

import com.github.thrsouza.sauron.application.UseCase;
import com.github.thrsouza.sauron.domain.customer.Customer;
import com.github.thrsouza.sauron.domain.customer.CustomerRepository;

public class CreateCustomerUseCase implements UseCase<CreateCustomerUseCaseInput, CreateCustomerUseCaseOutput> {
    private final CustomerRepository customerRepository;

    public CreateCustomerUseCase(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CreateCustomerUseCaseOutput handle(CreateCustomerUseCaseInput input) {
        Optional<Customer> existingCustomer = customerRepository.findByDocument(input.document());

        if (existingCustomer.isPresent()) {
            return new CreateCustomerUseCaseOutput(existingCustomer.get().id());
        }

        Customer customer = Customer.create(
            input.document(), 
            input.name(), 
            input.email());

        customerRepository.save(customer);

        return new CreateCustomerUseCaseOutput(customer.id());
    }
}
