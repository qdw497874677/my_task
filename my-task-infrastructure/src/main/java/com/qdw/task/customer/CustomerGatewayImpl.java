package com.qdw.task.customer;

import com.qdw.task.domain.customer.Customer;
import com.qdw.task.domain.customer.gateway.CustomerGateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerGatewayImpl implements CustomerGateway {
//    @Autowired
//    private CustomerMapper customerMapper;

    @Override
    public Customer getByById(String customerId){
//      CustomerDO customerDO = customerMapper.getById(customerId);
      //Convert to Customer
      return null;
    }
}
