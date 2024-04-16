package com.camunda.academy.handler;

import com.camunda.academy.services.CustomerService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreditDeductionHandler {

  private CustomerService customerService;

  @Autowired
  public CreditDeductionHandler(CustomerService customerService) {
    this.customerService = customerService;
  }

  @JobWorker(type = "credit-deduction", autoComplete = false)
  public void handleCreditDeduction(JobClient client, ActivatedJob job, @Variable String customerId,
    @Variable Number orderTotal) {
    System.out.println("Job handled: " + job.getType());

    Double openAmount = customerService.deductCredit(customerId, orderTotal.doubleValue());

    client.newCompleteCommand(job.getKey()).variables(Map.of("openAmount", openAmount)).send();
  }
}