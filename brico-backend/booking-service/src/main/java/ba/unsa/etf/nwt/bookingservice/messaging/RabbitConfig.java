package ba.unsa.etf.nwt.bookingservice.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // Exchange
    public static final String EXCHANGE = "brico.exchange";

    // Routing keys
    public static final String APPOINTMENT_CREATED_KEY  = "appointment.created";
    public static final String SLOT_RESERVED_KEY        = "salon.slot.reserved";
    public static final String SLOT_REJECTED_KEY        = "salon.slot.rejected";

    // Queues koje booking-service SLUŠA
    public static final String SLOT_RESERVED_QUEUE = "slot.reserved.queue";
    public static final String SLOT_REJECTED_QUEUE = "slot.rejected.queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue slotReservedQueue() {
        return QueueBuilder.durable(SLOT_RESERVED_QUEUE).build();
    }

    @Bean
    public Queue slotRejectedQueue() {
        return QueueBuilder.durable(SLOT_REJECTED_QUEUE).build();
    }

    @Bean
    public Binding slotReservedBinding() {
        return BindingBuilder.bind(slotReservedQueue()).to(exchange()).with(SLOT_RESERVED_KEY);
    }

    @Bean
    public Binding slotRejectedBinding() {
        return BindingBuilder.bind(slotRejectedQueue()).to(exchange()).with(SLOT_REJECTED_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
