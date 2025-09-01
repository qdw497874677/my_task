package com.qdw.task.api.task;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.qdw.task.dto.CustomerAddCmd;
import com.qdw.task.dto.CustomerListByNameQry;
import com.qdw.task.dto.data.CustomerDTO;

public interface CustomerServiceI {

    Response addCustomer(CustomerAddCmd customerAddCmd);

    MultiResponse<CustomerDTO> listByName(CustomerListByNameQry customerListByNameQry);
}
