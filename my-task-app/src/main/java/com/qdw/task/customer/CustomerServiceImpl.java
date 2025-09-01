package com.qdw.task.customer;

import com.alibaba.cola.catchlog.CatchAndLog;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.qdw.task.api.task.CustomerServiceI;
import com.qdw.task.customer.executor.CustomerAddCmdExe;
import com.qdw.task.customer.executor.query.CustomerListByNameQryExe;
import com.qdw.task.dto.CustomerAddCmd;
import com.qdw.task.dto.CustomerListByNameQry;
import com.qdw.task.dto.data.CustomerDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;


@Service
@CatchAndLog
public class CustomerServiceImpl implements CustomerServiceI {

    @Resource
    private CustomerAddCmdExe customerAddCmdExe;

    @Resource
    private CustomerListByNameQryExe customerListByNameQryExe;

    @Override
    public Response addCustomer(CustomerAddCmd customerAddCmd) {
        return customerAddCmdExe.execute(customerAddCmd);
    }

    @Override
    public MultiResponse<CustomerDTO> listByName(CustomerListByNameQry customerListByNameQry) {
        return customerListByNameQryExe.execute(customerListByNameQry);
    }

}