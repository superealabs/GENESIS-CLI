package handler;

import utils.FileUtils;
import genesis.config.Constantes;
import genesis.config.ApplicationType;
import genesis.config.langage.Language;

import java.util.HashMap;
import java.util.Scanner;
import java.io.IOException;

public class LanguageHandler {

    public Language chooseLanguage(Scanner scanner, ApplicationType application) throws IOException {
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        System.out.println("Choose your language: ");
        HashMap<Integer, Integer> counterLangageIdHashMap = new HashMap<>();
        for (int i = 0, j = 1; i < languages.length; i++) {
            if (languages[i].getApplicationId().contains(application.getId())) {
                counterLangageIdHashMap.put(j, languages[i].getId());
                System.out.println(j + ")  " + languages[i].getName());
                j++;
            }
        }
        System.out.print("> ");
        int choice = scanner.nextInt();
        System.out.println(counterLangageIdHashMap.get(choice).toString());
        return languages[counterLangageIdHashMap.get(choice)];
    }
}
