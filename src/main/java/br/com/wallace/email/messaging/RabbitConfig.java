package br.com.wallace.email.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    DirectExchange emailExchange(MessagingProperties props) {
        return new DirectExchange(props.exchange());
    }

    @Bean
    DirectExchange deadLetterExchange(MessagingProperties props) {
        return new DirectExchange(props.deadLetterExchange());
    }

    @Bean
    Queue emailQueue(MessagingProperties props) {
        return QueueBuilder.durable(props.queue())
                .deadLetterExchange(props.deadLetterExchange())
                .deadLetterRoutingKey(props.routingKey())
                .build();
    }

    @Bean
    Queue deadLetterQueue(MessagingProperties props) {
        return QueueBuilder.durable(props.deadLetterQueue()).build();
    }

    @Bean
    Binding emailBinding(Queue emailQueue, DirectExchange emailExchange, MessagingProperties props) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(props.routingKey());
    }

    @Bean
    Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange, MessagingProperties props) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(props.routingKey());
    }

    @Bean
    MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
