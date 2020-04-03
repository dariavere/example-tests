import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class KafkaClient {

    public static String BOOTSTRAP_SERVERS;

    public Producer createProducer() {
        KafkaProducer<String, String> producer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
                        ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                ),
                new StringSerializer(),
                new StringSerializer()
        );
        return producer;
    }

    public Consumer createConsumer() {
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS,
                        ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                ),
                new StringDeserializer(),
                new StringDeserializer()
        );
        return consumer;
    }

    public void createTopics(String topicName) throws InterruptedException, ExecutionException {
        Collection<NewTopic> collection = Collections.singleton(new NewTopic(topicName, 1, (short) 1));
        AdminClient kafkaAdminClient = AdminClient.create(Collections.singletonMap("bootstrap.servers", BOOTSTRAP_SERVERS));
        CreateTopicsResult createTopicsResult = kafkaAdminClient.createTopics(collection);
        createTopicsResult.all().get();
    }

    public void sendMessage(String topicName, String message) throws InterruptedException, ExecutionException {
        Producer producer = createProducer();
        producer.send(new ProducerRecord<>(topicName, message)).get();
    }

    public List<String> getMessages(String topicName) {
        Consumer consumer = createConsumer();
        consumer.subscribe(Arrays.asList(topicName));

        return getRecords(consumer);
    }

    private List<String> getRecords(Consumer consumer) {
        List<String> messages = new ArrayList<>();

        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));

        for (ConsumerRecord<String, String> record : records) {
            System.out.printf("topic = %s, offset = %d, message = %s" + "\n",
                    record.topic(), record.offset(), record.value());
            messages.add(record.value());
        }

        consumer.commitSync();
        return messages;
    }
}
