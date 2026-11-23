package br.com.wallace.email.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.messaging")
public record MessagingProperties(String exchange, String queue, String routingKey,
                                  String deadLetterExchange, String deadLetterQueue) {
}
