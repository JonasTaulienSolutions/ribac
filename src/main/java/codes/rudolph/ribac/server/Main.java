package codes.rudolph.ribac.server;

import com.google.inject.Guice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private final static Logger log = LoggerFactory.getLogger(Main.class);



    public static void main(String[] args) {
        try {
            final var injector = Guice.createInjector(new Module());

            final var server = injector.getInstance(Server.class);

            server.start();

        } catch (Exception e) {
            log.error("Failed to start ribac:", e);
        }
    }
}
