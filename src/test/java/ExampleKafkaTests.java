import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.junit.Rule;
import org.junit.Test;

import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;



import java.time.Duration;
import java.util.*;

import static org.apache.kafka.clients.admin.AdminClient.*;


public class ExampleKafkaTests {

    @Rule
    public KafkaContainer kafka = new KafkaContainer()
            .withExposedPorts(9092, 9093, 2181)
            .withStartupTimeout(Duration.ofSeconds(60));


    @Test
    public void kafkaStartTest() throws Exception {

        Collection<NewTopic> collection = Collections.singleton(new NewTopic("testTopic", 1, (short) 1));

        AdminClient kafkaAdminClient = create(Collections.singletonMap("bootstrap.servers", kafka.getBootstrapServers()));

        int kafkaPort = kafka.getMappedPort(9093);
        int zookeeperPort = kafka.getMappedPort(2181);

        CreateTopicsResult createTopicsResult = kafkaAdminClient.createTopics(collection);

        createTopicsResult.all().get();


        try (
                KafkaProducer<String, String> producer = new KafkaProducer<>(
                        ImmutableMap.of(
                                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                                ProducerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString()
                        ),
                        new StringSerializer(),
                        new StringSerializer()
                );

                KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                        ImmutableMap.of(
                                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers(),
                                ConsumerConfig.GROUP_ID_CONFIG, "tc-" + UUID.randomUUID(),
                                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
                        ),
                        new StringDeserializer(),
                        new StringDeserializer()
                );
        ) {
            String topicName = "testTopic";
            consumer.subscribe(Arrays.asList(topicName));

            producer.send(new ProducerRecord<>(topicName, "testMessage")).get();
            producer.send(new ProducerRecord<>(topicName, "dsdfsdf")).get();
            producer.send(new ProducerRecord<>(topicName, "werwerwer")).get();


                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("topic = %s, offset = %d, message = %s" + "\n",
                            record.topic(), record.offset(), record.value());
                }
                   consumer.commitSync();


            //consumer.unsubscribe();
        }
    }

}
