package handler;

import java.util.Scanner;
import genesis.config.Application;

public class ApplicationHandler {


    public Application chooseApplication(Scanner scanner) {
        Application app = new Application();
        System.out.println("Create a new project ? (y/n): ");
        System.out.print("> ");
        String choice = scanner.next().toUpperCase();
        switch (choice) {
            case "Y":
                System.out.println("Creating a new project...");
                app.setTrueApplication(true);
                break;
            case "N":
                System.out.println("Continuing an existing project...");
                app.setTrueApplication(false);
                break;
        }
        return app;
    }

}
