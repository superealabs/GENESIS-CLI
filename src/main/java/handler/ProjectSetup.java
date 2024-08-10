package handler;

import genesis.config.Constantes;
import genesis.config.Language;
import utils.FileUtils;

import java.io.IOException;
import java.util.Scanner;

public class ProjectSetup {

    public Language chooseLanguage(Scanner scanner) throws IOException {
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        System.out.println("Choose a langage:");
        for (int i = 0; i < languages.length; i++) {
            System.out.println((i + 1) + ") " + languages[i].getName());
        }
        System.out.print("> ");
        return languages[scanner.nextInt() - 1];
    }

    public Language chooseFramework(Scanner scanner) throws IOException {
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        System.out.println("Choose a framework:");
        for (int i = 0; i < languages.length; i++) {
            System.out.println((i + 1) + ") " + languages[i].getName());
        }
        System.out.print("> ");
        return languages[scanner.nextInt() - 1];
    }
}
