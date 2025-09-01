package com.qdw.task.domain.customer.gateway;

import com.qdw.task.domain.customer.Customer;

public interface CustomerGateway {
    Customer getByById(String customerId);
}
