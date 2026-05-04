package ba.unsa.etf.nwt.notificationservice.messaging;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "brico.exchange";

    // Routing keys koje slušamo
    public static final String SLOT_RESERVED_KEY  = "salon.slot.reserved";
    public static final String SLOT_REJECTED_KEY  = "salon.slot.rejected";
    public static final String NOTIFICATION_KEY   = "notification.push";

    // Queues
    public static final String NOTIF_CONFIRMED_QUEUE = "notif.appointment.confirmed.queue";
    public static final String NOTIF_CANCELLED_QUEUE = "notif.appointment.cancelled.queue";
    public static final String NOTIF_PUSH_QUEUE      = "notif.push.queue";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue notifConfirmedQueue() {
        return QueueBuilder.durable(NOTIF_CONFIRMED_QUEUE).build();
    }

    @Bean
    public Queue notifCancelledQueue() {
        return QueueBuilder.durable(NOTIF_CANCELLED_QUEUE).build();
    }

    @Bean
    public Queue notifPushQueue() {
        return QueueBuilder.durable(NOTIF_PUSH_QUEUE).build();
    }

    @Bean
    public Binding confirmedBinding() {
        return BindingBuilder.bind(notifConfirmedQueue()).to(exchange()).with(SLOT_RESERVED_KEY);
    }

    @Bean
    public Binding cancelledBinding() {
        return BindingBuilder.bind(notifCancelledQueue()).to(exchange()).with(SLOT_REJECTED_KEY);
    }

    @Bean
    public Binding pushBinding() {
        return BindingBuilder.bind(notifPushQueue()).to(exchange()).with(NOTIFICATION_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate t = new RabbitTemplate(cf);
        t.setMessageConverter(messageConverter());
        return t;
    }
}
