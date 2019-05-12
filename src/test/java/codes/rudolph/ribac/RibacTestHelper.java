package codes.rudolph.ribac;

import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.ext.web.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RibacTestHelper {

    public static void destroyRibacDb() throws InterruptedException, IOException {
        System.out.println("- Destroying Database");
        RibacTestHelper.executeShellCommand(new ProcessBuilder(
            "/bin/sh",
            "-c",
            "db_exec 'DROP DATABASE IF EXIST ${MYSQL_DATABASE};'"
        ));
    }



    public static void createRibacDb() throws IOException, InterruptedException {
        System.out.println("- Creating Database");
        RibacTestHelper.executeShellCommand(new ProcessBuilder(
            "/bin/sh",
            "-c",
            "db_exec 'CREATE DATABASE ${MYSQL_DATABASE};'"
        ));

        System.out.println("- Creating Tables");
        RibacTestHelper.executeShellCommand(new ProcessBuilder(
            "/bin/sh",
            "-c",
            "db_exec_script '${MYSQL_DATABASE}' '/docker-entrypoint-initdb.d/ribac.sql'"
        ));
    }



    public static WebClient createHttpClient() {
        final var options = new WebClientOptions().setDefaultHost("localhost")
                                                  .setDefaultPort(8080);

        return WebClient.create(Vertx.vertx(), options);
    }



    private static void executeShellCommand(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        var process = processBuilder.start();
        new BufferedReader(new InputStreamReader(process.getInputStream())).lines().forEach(line -> System.out.println("| STDOUT: " + line));
        new BufferedReader(new InputStreamReader(process.getErrorStream())).lines().forEach(line -> System.err.println("| STDERR: " + line));
        process.waitFor();
    }

}
