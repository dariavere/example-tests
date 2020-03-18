import org.junit.Before;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.time.Duration;
import java.util.function.Consumer;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

public class ExampleTests {

    public static String base_url = "http://localhost:";

    int wiremockPort;

    @Before
    public void tryToStartContainer() throws InterruptedException {

        GenericContainer wiremock = new GenericContainer(new ImageFromDockerfile()
                .withFileFromPath(".", Path.of("src/test/resources/wiremock")))
                .withExposedPorts(8080)
                .withStartupTimeout(Duration.ofSeconds(60));

        wiremock.start();

        wiremock.followOutput(new Consumer<OutputFrame>() {
            @Override
            public void accept(OutputFrame outputFrame) {
                System.out.println(String.format(
                        "%s: %s",
                        "wiremock",
                        outputFrame.getUtf8String()
                ));
            }
        });

        wiremockPort = wiremock.getMappedPort(8080);

        System.out.println(wiremockPort);

    }

    @Test
    public void getTest() {
        when().
                get(base_url + wiremockPort + "/test_wiremock").
                then().
                statusCode(200);
    }

    @Test
    public void postTest() {
        given().
                body("post_request_body.json").
                post(base_url + wiremockPort + "/test_wiremock/new_user").
                then().
                statusCode(201);
    }
}