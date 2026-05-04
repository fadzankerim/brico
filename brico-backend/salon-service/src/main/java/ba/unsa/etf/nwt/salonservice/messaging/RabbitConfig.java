package ba.unsa.etf.nwt.salonservice.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "brico.exchange";

    public static final String APPOINTMENT_CREATED_KEY  = "appointment.created";
    public static final String SLOT_RESERVED_KEY        = "salon.slot.reserved";
    public static final String SLOT_REJECTED_KEY        = "salon.slot.rejected";

    // Queue koju salon-service SLUŠA
    public static final String APPOINTMENT_CREATED_QUEUE = "appointment.created.queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue appointmentCreatedQueue() {
        return QueueBuilder.durable(APPOINTMENT_CREATED_QUEUE).build();
    }

    @Bean
    public Binding appointmentCreatedBinding() {
        return BindingBuilder.bind(appointmentCreatedQueue()).to(exchange()).with(APPOINTMENT_CREATED_KEY);
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
