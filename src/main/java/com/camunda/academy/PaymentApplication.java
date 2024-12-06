package com.camunda.academy;

import com.camunda.academy.handler.CreditCardChargingHandler;
import com.camunda.academy.handler.CreditDeductionHandler;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.worker.JobWorker;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProvider;
import io.camunda.zeebe.client.impl.oauth.OAuthCredentialsProviderBuilder;

import java.net.URI;
import java.util.Scanner;

public class PaymentApplication {

  // Zeebe client credentials
  private static final String ZEEBE_GRPC_ADDRESS = "grpcs://xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.xxx-1.zeebe.camunda.io:443";
  private static final String ZEEBE_REST_ADDRESS = "https://xxx-1.zeebe.camunda.io/xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";
  private static final String ZEEBE_CLIENT_ID = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
  private static final String ZEEBE_CLIENT_SECRET = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
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
            .grpcAddress(URI.create(ZEEBE_GRPC_ADDRESS))
            .restAddress(URI.create(ZEEBE_REST_ADDRESS))
            .credentialsProvider(credentialsProvider)
            .build()) {

      // Request the cluster topology
      System.out.println("Connected to: " + client.newTopologyRequest().send().join());

      // Start a job worker
      final JobWorker creditDeductionWorker = client.newWorker()
              .jobType("credit-deduction")
              .handler(new CreditDeductionHandler())
              .open();

      final JobWorker creditCardChargingWorker = client.newWorker()
              .jobType("credit-card-charging")
              .handler(new CreditCardChargingHandler())
              .open();

      // Terminate the worker with an Integer input
      Scanner sc = new Scanner(System.in);
      sc.nextInt();
      sc.close();
      creditCardChargingWorker.close();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}