package com.camunda.academy.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.util.Map;
import java.util.Random;

public class PaymentInvocationHandler implements JobHandler {

  private ZeebeClient zeebeClient;

  public PaymentInvocationHandler(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  @Override
  public void handle(JobClient jobClient, ActivatedJob job) throws Exception {
    System.out.println("Job handled: " + job.getType());

    Integer orderId = new Random().nextInt();
    Map<String, Object> variables = job.getVariablesAsMap();
    variables.put("orderId", orderId);

    zeebeClient.newPublishMessageCommand()
      .messageName("paymentRequestedMessage")
      .withoutCorrelationKey()
      .variables(variables)
      .send();

    jobClient.newCompleteCommand(job)
      .variable("orderId", orderId).send();
  }
}