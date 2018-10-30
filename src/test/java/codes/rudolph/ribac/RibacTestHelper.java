package codes.rudolph.ribac;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RibacTestHelper {

    public static void destroyRibacDb() throws InterruptedException, IOException {
        RibacTestHelper.executeShellCommand("docker-compose rm --stop --force ribac-db");
    }



    public static void createRibacDb() throws IOException, InterruptedException {
        RibacTestHelper.executeShellCommand("docker-compose up --detach ribac-db");
        Thread.sleep(10000);
    }



    public static WebClient createHttpClient() {
        final var options = new WebClientOptions().setDefaultHost("localhost")
                                                  .setDefaultPort(8080);

        return WebClient.create(Vertx.vertx(), options);
    }



    private static void executeShellCommand(String command) throws IOException, InterruptedException {
        var process = Runtime.getRuntime().exec(command);
        new BufferedReader(new InputStreamReader(process.getInputStream())).lines().forEach(System.out::println);
        new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().forEach(System.err::println);
        process.waitFor();
    }

}
