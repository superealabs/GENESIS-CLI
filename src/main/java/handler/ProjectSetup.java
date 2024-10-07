package handler;

import genesis.config.Constantes;
import genesis.config.CustomFile;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import utils.FileUtils;

import java.io.File;
import java.util.Scanner;

public class ProjectSetup {
    public String setupProject(Scanner scanner, Framework framework) throws Exception {
        System.out.print("Enter your project name: ");
        String projectName = scanner.nextLine();

        File project = new File(projectName);
        project.mkdir();

        for (CustomFile c : framework.getAdditionnalFiles()) {
            String customFilePath = c.getName().replace("[projectNameMaj]", FileUtils.majStart(projectName));
            FileUtils.createFileStructure(customFilePath);
            String customFileContent = FileUtils.getFileContent(Constantes.DATA_PATH + "/" + c.getContent())
                    .replace("[projectNameMaj]", FileUtils.majStart(projectName));
            FileUtils.overwriteFileContent(customFilePath, customFileContent);
        }
        return projectName;
    }
}
