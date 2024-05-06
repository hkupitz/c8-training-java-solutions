package com.camunda.academy.handler;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.stereotype.Component;

@Component
public class CreditCardChargingHandler {

  @JobWorker(type = "credit-card-charging", autoComplete = false)
  public void handleCreditDeduction(JobClient client, ActivatedJob job) {
    System.out.println("Job handled: " + job.getType());

    client.newCompleteCommand(job.getKey()).send();
  }
}