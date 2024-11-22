package handler;

import genesis.config.Constantes;
import genesis.config.langage.Language;
import utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class LanguageHandler {

    public Language chooseLanguage(Scanner scanner) throws IOException {
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        System.out.println("Choose your language: ");
        HashMap<Integer, Integer> counterLangageIdHashMap = new HashMap<>();
        for (int i = 0, j = 1; i < languages.length; i++) {
            counterLangageIdHashMap.put(j, i + 1);
            System.out.println(j + ") " + languages[i].getName());
            j++;
        }
        System.out.print("> ");
        int choice = scanner.nextInt();
        System.out.println(counterLangageIdHashMap.get(choice).toString());
        return languages[counterLangageIdHashMap.get(choice)];
    }
}