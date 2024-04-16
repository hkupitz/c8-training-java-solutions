package com.camunda.academy.handler;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

public class CreditDeductionHandler implements JobHandler {

  @Override
  public void handle(JobClient client, ActivatedJob job) {
    System.out.println("Job handled: " + job.getType());

    client.newCompleteCommand(job.getKey()).send();
  }
}