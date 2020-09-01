package de.gigo.pdfcreator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class KafkaConfig {

    @Value("${my.group.id}")
    private String groupId;

    @Value("${pdf.replies}")
    private String replyTopic;

    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") ProducerFactory<String, String> pf,
                                                                               @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") ConcurrentKafkaListenerContainerFactory<String, String> factory) {
        ConcurrentMessageListenerContainer<String, String> replyContainer = factory.createContainer(replyTopic);
        replyContainer.getContainerProperties().setMissingTopicsFatal(false);
        replyContainer.getContainerProperties().setGroupId(groupId);
        return new ReplyingKafkaTemplate<>(pf, replyContainer);
    }

    @Bean
    public KafkaTemplate<String, String> replyTemplate(@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") ProducerFactory<String, String> pf,
                                                       @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") ConcurrentKafkaListenerContainerFactory<String, String> factory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(pf);
        factory.getContainerProperties().setMissingTopicsFatal(false);
        factory.setReplyTemplate(kafkaTemplate);
        return kafkaTemplate;
    }


}
