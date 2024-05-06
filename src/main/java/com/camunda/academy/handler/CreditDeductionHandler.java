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

  @JobWorker(type = "credit-deduction", autoComplete = false)
  public void handleCreditDeduction(JobClient client, ActivatedJob job) {
    System.out.println("Job handled: " + job.getType());

    client.newCompleteCommand(job.getKey()).send();
  }
}