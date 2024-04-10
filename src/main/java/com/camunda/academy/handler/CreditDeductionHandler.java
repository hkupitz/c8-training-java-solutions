package com.camunda.academy.handler;

import com.camunda.academy.services.CustomerService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

public class CreditDeductionHandler implements JobHandler {

  private CustomerService customerService = new CustomerService();

  @Override
  public void handle(JobClient client, ActivatedJob job) throws Exception {
    System.out.println("Job handled: " + job.getType());

    String customerId = (String) job.getVariable("customerId");
    Number orderTotal = (Number) job.getVariable("orderTotal");

    Double openAmount = customerService.deductCredit(customerId, orderTotal.doubleValue());
    client.newCompleteCommand(job).variable("openAmount", openAmount).send();
  }
}