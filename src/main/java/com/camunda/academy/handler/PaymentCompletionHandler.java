package com.camunda.academy.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletionHandler {

  private final ZeebeClient zeebeClient;

  @Autowired
  public PaymentCompletionHandler(ZeebeClient zeebeClient) {
    this.zeebeClient = zeebeClient;
  }

  @JobWorker(type = "payment-completion", autoComplete = false)
  public void handlePaymentCompletion(JobClient jobClient, ActivatedJob job) {
    System.out.println("Job handled: " + job.getType());

    // Tell order process to continue
    zeebeClient.newPublishMessageCommand()
      .messageName("paymentCompletedMessage")
      .correlationKey(String.valueOf(job.getVariable("orderId")))
      .send().join();

    // Complete the job in the payment process
    jobClient.newCompleteCommand(job.getKey()).send();
  }
}