package handler;

import utils.FileUtils;
import genesis.config.Constantes;   
import genesis.config.langage.Language;
import genesis.config.langage.Framework;

import java.util.Scanner;
import java.io.IOException;

public class FrameworkHandler {

    public Framework chooseFramework(Scanner scanner, Language language) throws IOException {
        System.out.println("Choose a framework:");
        Framework[] frameworks_mvc = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));
        for (int i = 1; i < frameworks_mvc.length; i++) {
            if ( frameworks_mvc[i].getLangageId() == language.getId() ) {
                System.out.println((i + 1) + ") " + frameworks_mvc[i].getName());
            }
        }
        System.out.print(">");
        return frameworks_mvc[scanner.nextInt() -1];
    }

}
