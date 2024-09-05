package handler;

import utils.FileUtils;
import genesis.config.Constantes;
import genesis.config.ApplicationType;

import java.util.Scanner;
import java.io.IOException;

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
