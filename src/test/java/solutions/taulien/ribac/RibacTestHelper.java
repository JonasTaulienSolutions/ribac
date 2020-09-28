package solutions.taulien.ribac;

import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.ext.web.client.HttpResponse;
import io.vertx.reactivex.ext.web.client.WebClient;
import org.junit.jupiter.api.TestInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RibacTestHelper {

    public static final String HEADER_ACCEPT = HttpHeaders.ACCEPT.toString();

    public static final String MIME_APPLICATION_JSON = HttpHeaderValues.APPLICATION_JSON.toString();

    public static final String HEADER_CONTENT_TYPE = HttpHeaders.CONTENT_TYPE.toString();

    private static final String BACKUP_FILE_NAME = "pre-test.sql";



    public static void createRibacDbBackup() throws InterruptedException, IOException {
        System.out.println("- Creating Database Backup");
        RibacTestHelper.executeShellCommand(new ProcessBuilder(
            "/bin/sh",
            "-c",
            "db_create_backup '${MYSQL_DATABASE}' '" + BACKUP_FILE_NAME + "'"
        ));
    }



    public static void restoreRibacDbBackup() throws InterruptedException, IOException {
        System.out.println("- Restoring Database Backup");
        RibacTestHelper.executeShellCommand(new ProcessBuilder(
            "/bin/sh",
            "-c",
            "db_restore_backup '${MYSQL_DATABASE}' '" + BACKUP_FILE_NAME + "'"
        ));
    }



    public static void destroyRibacDb() throws InterruptedException, IOException {
        System.out.println("- Destroying Database");
        RibacTestHelper.executeShellCommand(new ProcessBuilder(
            "/bin/sh",
            "-c",
            "db_exec 'DROP DATABASE IF EXISTS ${MYSQL_DATABASE};'"
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
            "db_exec_script '${MYSQL_DATABASE}' './ribac.sql'"
        ));
    }



    public static WebClient createHttpClient() {
        final var options = new WebClientOptions().setDefaultHost("localhost")
                                                  .setDefaultPort(8080);

        return WebClient.create(Vertx.vertx(), options);
    }



    public static String urlEncode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8).replace("+", "%20");
    }



    public static void assetStatusCodeEquals(HttpResponse<Buffer> response, int expectedStatusCode) {
        assertEquals(
            expectedStatusCode,
            response.statusCode(),
            () -> "Unexpected status code. Response body: '" + response.bodyAsString() + "'"
        );
    }



    public static void assertBodyEquals(HttpResponse<Buffer> response, JsonObject expectedBody) {
        assertEquals(
            MIME_APPLICATION_JSON,
            response.getHeader(HEADER_CONTENT_TYPE),
            () -> "Unexpected content type. Response body: '" + response.bodyAsString() + "'"
        );

        assertEquals(
            expectedBody,
            response.bodyAsJsonObject()
        );
    }



    public static void assertStatusCodeAndBodyEquals(HttpResponse<Buffer> response, int expectedStatusCode, JsonObject expectedBody) {
        RibacTestHelper.assetStatusCodeEquals(response, expectedStatusCode);
        RibacTestHelper.assertBodyEquals(response, expectedBody);
    }



    public static HttpResponse<Buffer> post(WebClient client, TestInfo testInfo, String path, JsonObject body) {
        return client.post(path)
                     .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                     .putHeader("Request-Id", testInfo.getDisplayName())
                     .rxSendJsonObject(body)
                     .blockingGet();
    }



    public static HttpResponse<Buffer> postWithoutBody(WebClient client, TestInfo testInfo, String path) {
        return client.post(path)
                     .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                     .putHeader("Request-Id", testInfo.getDisplayName())
                     .rxSend()
                     .blockingGet();
    }



    public static HttpResponse<Buffer> get(WebClient client, TestInfo testInfo, String path) {
        return client.get(path)
                     .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                     .putHeader("Request-Id", testInfo.getDisplayName())
                     .rxSend()
                     .blockingGet();
    }



    public static HttpResponse<Buffer> delete(WebClient client, TestInfo testInfo, String path) {
        return client.delete(path)
                     .putHeader(HEADER_ACCEPT, MIME_APPLICATION_JSON)
                     .putHeader("Request-Id", testInfo.getDisplayName())
                     .rxSend()
                     .blockingGet();
    }



    public static JsonObject createErrorResponseBody(TestInfo testInfo, String expectedMessage) {
        return new JsonObject().put(
            "error", new JsonObject()
                         .put("message", expectedMessage)
                         .put("requestId", testInfo.getDisplayName())
        );
    }



    private static void executeShellCommand(ProcessBuilder processBuilder) throws IOException, InterruptedException {
        var process = processBuilder.start();
        new BufferedReader(new InputStreamReader(process.getInputStream()))
            .lines()
            .forEach(line -> System.out.println("| STDOUT: " + line));
        new BufferedReader(new InputStreamReader(process.getErrorStream()))
            .lines()
            .filter(line -> !line.endsWith("[Warning] Using a password on the command line interface can be insecure."))
            .forEach(line -> System.err.println("| STDERR: " + line));
        process.waitFor();
    }

}
