package com.camunda.academy.handler;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentCompletionHandler {

  @Autowired
  private ZeebeClient zeebeClient;

  @JobWorker(type = "payment-completion", autoComplete = false)
  public void handlePaymentCompletion(JobClient jobClient, ActivatedJob job) {
    System.out.println("Job handled: " + job.getType());

    // Get message name from input variable to differentiate between success & failure
    String messageName = (String) job.getVariable("messageName");

    // Tell order process to continue
    zeebeClient.newPublishMessageCommand()
      .messageName(messageName)
      .correlationKey(String.valueOf(job.getVariable("orderId")))
      .send().join();

    // Complete the job in the payment process
    jobClient.newCompleteCommand(job.getKey()).send();
  }
}