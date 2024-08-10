package handler;

import genesis.config.Application;
import genesis.config.ApplicationType;
import genesis.config.Constantes;
import utils.FileUtils;

import java.io.IOException;
import java.util.Scanner;

public class ApplicationTypeHandler {

    public ApplicationType chooseTypeApplication(Scanner scanner) throws IOException {
        ApplicationType[] applications = FileUtils.fromJson(ApplicationType[].class, FileUtils.getFileContent(Constantes.APPLICATION_JSON));
        System.out.println("Choose your project type: ");
        for (int i = 0; i < applications.length; i++) {
            System.out.println((i + 1) + ") " + applications[i].getName());
        }
        System.out.print("> ");
        return applications[scanner.nextInt() - 1];
    }
}
