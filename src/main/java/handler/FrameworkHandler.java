package handler;

import utils.FileUtils;
import genesis.config.ApplicationType;
import genesis.config.langage.Language;
import genesis.config.langage.Framework;

import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;

public class FrameworkHandler {

    public Framework chooseFramework(Scanner scanner, ApplicationType applicationType, Language language) throws IOException {
        Framework[] frameworks = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(applicationType.getPath()));
        System.out.println(applicationType.getPath());
        System.out.println("Choose a framework:");
        HashMap<Integer, Integer> frames = new HashMap<>();
        for (int i = 0, j =1 ; i < frameworks.length; i++) {
            System.out.println(language.getId());
            if (frameworks[i].getLangageId() == language.getId()) {
                frames.put((i + 1), frameworks[i].getId());
                System.out.println(j + ")" + frameworks[i].getName());
                j++;
            }
        }
        System.out.print("> ");
        int choice = scanner.nextInt();
        return frameworks[frames.get(choice)];
    }

}
