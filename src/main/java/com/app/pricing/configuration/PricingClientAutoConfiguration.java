package com.app.pricing.configuration;

import com.app.pricing.grpc.PricingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Automatically configures the gRPC client for the Pricing Service. Enabled only if
 * 'spring.grpc.client.channels.pricingService.address' is present.
 */
@Configuration
@ConditionalOnProperty("spring.grpc.client.channels.pricingService.address")
public class PricingClientAutoConfiguration {

  @Value("${spring.grpc.client.channels.pricingService.address}")
  private String pricingServiceAddress;

  @Bean
  public PricingServiceGrpc.PricingServiceBlockingStub pricingServiceBlockingStub() {
    String address = pricingServiceAddress.replace("static://", "");
    String host = address.split(":")[0];
    int port = Integer.parseInt(address.split(":")[1]);

    ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

    return PricingServiceGrpc.newBlockingStub(channel);
  }
}
