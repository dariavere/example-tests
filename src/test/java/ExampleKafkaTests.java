import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;

import java.time.Duration;
import java.util.concurrent.ExecutionException;


public class ExampleKafkaTests {

    private KafkaClient kafkaClient = new KafkaClient();

    public String topicName = "testTopic";


    @Rule
    public KafkaContainer kafka = new KafkaContainer()
            .withExposedPorts(9092, 9093, 2181)
            .withStartupTimeout(Duration.ofSeconds(60));

    @Before
    public void init() throws ExecutionException, InterruptedException {
        KafkaClient.BOOTSTRAP_SERVERS = kafka.getBootstrapServers();
        kafkaClient.createTopics(topicName);
    }

    @Test
    public void kafkaMessageTest() throws ExecutionException, InterruptedException {

        /*        int kafkaPort = kafka.getMappedPort(9093);
        int zookeeperPort = kafka.getMappedPort(2181);*/

        kafkaClient.sendMessage(topicName, "TestMessage1");
        kafkaClient.sendMessage(topicName, "TestMessage2");
        kafkaClient.sendMessage(topicName, "TestMessage3");

        kafkaClient.getMessages(topicName);


    }
}
