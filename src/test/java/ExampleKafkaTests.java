import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;


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

    }
}
