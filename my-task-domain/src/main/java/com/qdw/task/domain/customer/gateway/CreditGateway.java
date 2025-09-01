package com.qdw.task.domain.customer.gateway;

import com.qdw.task.domain.customer.Credit;

//Assume that the credit info is in another distributed Service
public interface CreditGateway {
    Credit getCredit(String customerId);
}
