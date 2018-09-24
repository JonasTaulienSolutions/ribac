package codes.rudolph.ribac;

import com.google.inject.Guice;

public class Main {
    public static void main(String[] args) {
        try {
            final var injector = Guice.createInjector(new Module());

            final var app = injector.getInstance(Server.class);

            app.start();

        } catch (Exception e) {
            System.out.println("Failed to start:");
            e.printStackTrace();
        }
    }
}
