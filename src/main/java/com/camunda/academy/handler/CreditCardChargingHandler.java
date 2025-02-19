package com.camunda.academy.handler;

import com.camunda.academy.services.CreditCardService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CreditCardChargingHandler {

  private final CreditCardService creditCardService;

  @Autowired
  public CreditCardChargingHandler(CreditCardService creditCardService) {
    this.creditCardService = creditCardService;
  }

  @JobWorker(type = "credit-card-charging", autoComplete = false)
  public void handleCreditDeduction(JobClient client, ActivatedJob job,
    @Variable String cardNumber, @Variable String expiryDate, @Variable String cvc,
    @Variable Double openAmount) {
    System.out.println("Job handled: " + job.getType());

    try {
      creditCardService.chargeAmount(cardNumber, cvc, expiryDate, openAmount);
      client.newCompleteCommand(job.getKey()).send();
    } catch (IllegalArgumentException exception) {
      client.newFailCommand(job.getKey()).retries(0).retryBackoff(Duration.ZERO).errorMessage(exception.getMessage())
        .send().join();
    }
  }
}