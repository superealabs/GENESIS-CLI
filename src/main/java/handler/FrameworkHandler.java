package handler;

import utils.FileUtils;
import genesis.config.Constantes;
import genesis.config.ApplicationType;
import genesis.config.langage.Language;
import genesis.config.langage.Framework;

import java.util.Scanner;
import java.io.IOException;

public class FrameworkHandler {

    public Framework chooseFramework(Scanner scanner, ApplicationType applicationType, Language language) throws IOException {
        System.out.println("Choose a framework:");
        if (applicationType.getId() == 1) {
            return getMVCFramework(scanner,language);
        } else if (applicationType.getId() == 2) {
            return getAPIFramework(scanner,language);
        }
        System.out.print("> ");
        return null;
    }

    private Framework getMVCFramework(Scanner scanner, Language language) throws IOException {
        Framework[] frameworks_mvc = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_MVC_JSON));
        for (int i = 1; i < frameworks_mvc.length; i++) {
            if ( frameworks_mvc[i].getLangageId() == language.getId() ) {
                System.out.println((i + 1) + ") " + frameworks_mvc[i].getName());
            }
        }
        System.out.print(">");
        return frameworks_mvc[scanner.nextInt() -1];
    }

    private Framework getAPIFramework(Scanner scanner, Language language) throws IOException {
        Framework[] frameworks_api = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_API_JSON));
        for (int j = 1; j < frameworks_api.length; j++) {
            if ( frameworks_api[j].getLangageId() == language.getId() ) {
                System.out.println((j + 1) + ") " + frameworks_api[j].getName());
            }
        }
        System.out.print(">");
        return frameworks_api[scanner.nextInt() -1];
    }

}
