package codes.rudolph.ribac;

import com.google.inject.Guice;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
        try {
            final var injector = Guice.createInjector(new Module());

            final var server = injector.getInstance(Server.class);

            server.start();

        } catch (Exception e) {
            final var log = Logger.getLogger(Main.class.getName());

            log.log(Level.SEVERE, "Failed to start:", e);
        }
    }
}
