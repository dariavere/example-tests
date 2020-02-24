import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.OutputFrame;
import org.testcontainers.images.builder.ImageFromDockerfile;

import java.nio.file.Path;
import java.util.function.Consumer;

import static com.jayway.restassured.RestAssured.when;

public class ExampleGetTest {

    @Test
    public void try_to_start_container() throws InterruptedException {

        GenericContainer wiremock;

        wiremock = new GenericContainer(new ImageFromDockerfile()
                .withFileFromPath(".", Path.of("src/test/resources/wiremock")));

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

        int wiremockPort = wiremock.getMappedPort(8080);
        System.out.println(wiremockPort);

        Thread.sleep(5000);

        String base_url = "http://localhost:";

        when().
                get(base_url + wiremockPort + "/test_wiremock").
                then().
                statusCode(200);


    }


}
