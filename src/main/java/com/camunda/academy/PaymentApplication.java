package com.camunda.academy;

import com.camunda.academy.handler.CreditCardChargingHandler;
import com.camunda.academy.handler.CreditDeductionHandler;
import com.camunda.academy.handler.PaymentCompletionHandler;
import com.camunda.academy.handler.PaymentInvocationHandler;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;
import java.net.URI;
import java.util.Scanner;

public class PaymentApplication {

  // Zeebe Client Credentials
  private static final String ZEEBE_ADDRESS = "https://0a9966d0-3a4f-4769-b5aa-b147aa6eed11.bru-2.zeebe.camunda.io:443";
  private static final String ZEEBE_CLIENT_ID = "5gNPsTqiyJ~FAfN3UMd_LBF223BnHID~";
  private static final String ZEEBE_CLIENT_SECRET = "Ugx0Cd~vinxciU7r~AMU3el-8.IZH3A2M4NCDb9X-SQNDq6X8u-4t9t~H~DRK73z";
  private static final String ZEEBE_AUTHORIZATION_SERVER_URL = "https://login.cloud.camunda.io/oauth/token";
  private static final String ZEEBE_TOKEN_AUDIENCE = "zeebe.camunda.io";

  public static void main(String[] args) {

    final OAuthCredentialsProvider credentialsProvider = new OAuthCredentialsProviderBuilder()
      .authorizationServerUrl(ZEEBE_AUTHORIZATION_SERVER_URL)
      .audience(ZEEBE_TOKEN_AUDIENCE)
      .clientId(ZEEBE_CLIENT_ID)
      .clientSecret(ZEEBE_CLIENT_SECRET)
      .build();

    try (final ZeebeClient client = ZeebeClient.newClientBuilder()
      .grpcAddress(URI.create(ZEEBE_ADDRESS))
      .credentialsProvider(credentialsProvider)
      .build()) {

      // Request the Cluster Topology
      System.out.println("Connected to: " + client.newTopologyRequest().send().join());

      // Start a Job Worker
      final JobWorker creditWorker = client.newWorker()
        .jobType("credit-deduction")
        .handler(new CreditDeductionHandler())
        .open();

      final JobWorker creditCardChargingWorker = client.newWorker()
        .jobType("credit-card-charging")
        .handler(new CreditCardChargingHandler())
        .open();

      final JobWorker paymentInvocationWorker = client.newWorker()
        .jobType("payment-invocation")
        .handler(new PaymentInvocationHandler(client))
        .open();

      final JobWorker paymentCompletionWorker = client.newWorker()
        .jobType("payment-completion")
        .handler(new PaymentCompletionHandler(client))
        .open();

      // Terminate the worker with an Integer input
      Scanner sc = new Scanner(System.in);
      sc.nextInt();
      sc.close();

      creditWorker.close();
      creditCardChargingWorker.close();
      paymentInvocationWorker.close();
      paymentCompletionWorker.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
