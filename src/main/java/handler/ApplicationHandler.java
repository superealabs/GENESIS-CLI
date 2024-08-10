package handler;

import genesis.config.Application;
import genesis.config.Constantes;
import genesis.connexion.Database;
import utils.FileUtils;

import java.io.IOException;
import java.util.Scanner;

public class ApplicationHandler {

    Application app = new Application();

    public Application chooseApplication(Scanner scanner) throws IOException {
        System.out.println("Create a new project ? (y/n): ");
        System.out.print("> ");
        String choice = scanner.next().toUpperCase();
        switch (choice) {
            case "Y":
                System.out.println("Creating a new project...");
                app.setTrueApplication(true);
            case "N":
                System.out.println("Continuing an existing project...");
                app.setTrueApplication(false);
        }
        return app;
    }

    public Application chooseTypeApplication(Scanner scanner) throws IOException {
        Application[] applications = FileUtils.fromJson(Application[].class, FileUtils.getFileContent(Constantes.APPLICATION_JSON));
        System.out.println("Choose your project type: ");
        for (int i = 0; i < applications.length; i++) {
            System.out.println((i + 1) + ") " + applications[i].getNom());
        }
        System.out.print("> ");
        return applications[scanner.nextInt() - 1];
    }

}
