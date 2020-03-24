import org.junit.Rule;
import org.junit.Test;
import org.testcontainers.containers.KafkaContainer;


import java.time.Duration;


public class ExampleKafkaTests {

    @Rule
    public KafkaContainer kafka = new KafkaContainer()
            .withExposedPorts(9092, 9093, 2181)
            .withStartupTimeout(Duration.ofSeconds(60));


    @Test
    public void kafkaStartTest() {
        kafka.getBootstrapServers();
    }
}
