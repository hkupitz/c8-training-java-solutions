package com.camunda.academy.services;

public class CreditCardService {

  public void chargeAmount(String cardNumber, String cvc, String expiryDate, Double amount) {
    System.out.printf("charging card %s that expires on %s and has cvc %s with amount of %f %s",
      cardNumber, expiryDate, cvc, amount, System.lineSeparator());

    if (!isValidExpiryDate(expiryDate)) {
      System.out.println("invalid expiry date");
      throw new IllegalArgumentException("Invalid expiry date");
    } else {
      System.out.println("payment completed");
    }
  }

  public Boolean isValidExpiryDate(String expiryDate) {
    return expiryDate.length() == 5;
  }
}