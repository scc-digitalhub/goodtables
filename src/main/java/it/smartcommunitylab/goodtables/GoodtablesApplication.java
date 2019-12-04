package it.smartcommunitylab.goodtables;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GoodtablesApplication {

    public static void main(String[] args) {
        SpringApplication.run(GoodtablesApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            printBanner();
        };
    }

    public void printBanner() {
        System.out.println("===========================================================  ");
        System.out.println("     ___                _ _____      _     _                 ");
        System.out.println("    / _ \\___   ___   __| /__   \\__ _| |__ | | ___ ___      ");
        System.out.println("   / /_\\/ _ \\ / _ \\ / _` | / /\\/ _` | '_ \\| |/ _ / __|  ");
        System.out.println("  / /_\\| (_) | (_) | (_| |/ / | (_| | |_) | |  __\\__ \\    ");
        System.out.println("  \\____/\\___/ \\___/ \\__,_|\\/   \\__,_|_.__/|_|\\___|___/");
        System.out.println("    :ready:                                                  ");
        System.out.println("===========================================================  ");

    }
}
