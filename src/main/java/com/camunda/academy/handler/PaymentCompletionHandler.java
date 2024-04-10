package com.camunda.academy.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

public class PaymentCompletionHandler implements JobHandler {

  private ZeebeClient zeebeClient;

  public PaymentCompletionHandler(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    System.out.println("Job handled: " + job.getType());

    String orderId = String.valueOf(job.getVariable("orderId"));
    zeebeClient.newPublishMessageCommand()
      .messageName("paymentCompletedMessage")
      .correlationKey(orderId)
      .send();

    jobClient.newCompleteCommand(job).send();
  }
}