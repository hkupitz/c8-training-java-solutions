package com.camunda.academy.handler;

import com.camunda.academy.services.CreditCardService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;
import java.time.Duration;

public class CreditCardChargingHandler implements JobHandler {

	private CreditCardService creditCardService = new CreditCardService();

	@Override
	public void handle(JobClient client, ActivatedJob job) throws Exception {
		System.out.println("Job handled: " + job.getType());

		String cardNumber = (String) job.getVariable("cardNumber");
		String cvc = (String) job.getVariable("cvc");
		String expiryDate = (String) job.getVariable("expiryDate");
		Number openAmount = (Number) job.getVariable("openAmount");

		try {
			creditCardService.chargeAmount(cardNumber, cvc, expiryDate, openAmount.doubleValue());
			client.newCompleteCommand(job).send();
		} catch (IllegalArgumentException e) {
			client.newThrowErrorCommand(job)
				.errorCode("invalidExpiryDateError")
				.send().join();
		} catch (Exception e) {
			client.newFailCommand(job)
				.retries(job.getRetries() - 1)
				.errorMessage(e.getMessage())
				.send();
		}
	}
}