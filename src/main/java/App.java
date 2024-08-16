import java.util.Scanner;

import handler.*;
import genesis.config.*;
import genesis.connexion.Database;
import genesis.connexion.Credentials;
import genesis.config.langage.Language;
import genesis.config.langage.Framework;

public class App {
    public static void main(String[] args) throws Throwable {
/*
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Database database;
        Language language;
        String databaseName, user, pwd, host;
        boolean useSSL, allowPublicKeyRetrieval;
        String projectName, entityName;
        Credentials credentials;
        String projectNameTagPath, projectNameTagContent;
        File project;
        String customFilePath, customFileContentOuter;
        Entity[] entities;
        String[] models, controllers, views;
        String modelFile, controllerFile, viewFile, customFile;
        String customFileContent;
        StringBuilder foreignContext;
        StringBuilder customChanges;
        String changesFile;
        StringBuilder navLink;
        String navLinkPath;
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Choose a database engine:");
            for (int i = 0; i < databases.length; i++) {
                System.out.println((i + 1) + ") " + databases[i].getName());
            }
            System.out.print("> ");
            database = databases[scanner.nextInt() - 1];
            System.out.println("Choose a framework:");
            for (int i = 0; i < languages.length; i++) {
                System.out.println((i + 1) + ") " + languages[i].getName());
            }
            System.out.print("> ");
            language = languages[scanner.nextInt() - 1];
            System.out.println("Enter your database credentials:");
            System.out.print("Database name: ");
            databaseName = scanner.next();
            System.out.print("Username: ");
            user = scanner.next();
            System.out.print("Password: ");
            pwd = scanner.next();
            System.out.print("Database host: ");
            host = scanner.next();
            System.out.print("Use SSL ?(Y/n): ");
            useSSL = scanner.next().equalsIgnoreCase("Y");
            System.out.print("Allow public key retrieval ?(Y/n): ");
            allowPublicKeyRetrieval = scanner.next().equalsIgnoreCase("Y");
            System.out.println();
            System.out.print("Enter your project name: ");
            projectName = scanner.next();
            System.out.print("Which entities to import ?(* to select all): ");
            entityName = scanner.next();
            credentials = new Credentials(databaseName, user, pwd, host, useSSL, allowPublicKeyRetrieval);
            project = new File(projectName);
            project.mkdir();
            for (CustomFile c : language.getAdditionnalFiles()) {
                customFilePath = c.getName();
                customFilePath = customFilePath.replace("[projectNameMaj]", FileUtils.majStart(projectName));
                FileUtils.createFile(customFilePath);
                customFileContentOuter = FileUtils.getFileContent(Constantes.DATA_PATH + "/" + c.getContent()).replace("[projectNameMaj]", FileUtils.majStart(projectName));
                FileUtils.overwriteFileContent(customFilePath, customFileContentOuter);
            }
            FileUtils.extractDir(Constantes.DATA_PATH + "/" + language.getSkeleton() + "." + Constantes.SKELETON_EXTENSION, project.getPath());
            for (String replace : language.getProjectNameTags()) {
                projectNameTagPath = replace.replace("[projectNameMaj]", FileUtils.majStart(projectName)).replace("[projectNameMin]", FileUtils.minStart(projectName));
                projectNameTagContent = FileUtils.getFileContent(projectNameTagPath).replace("[projectNameMaj]", FileUtils.majStart(projectName));
                projectNameTagContent = projectNameTagContent.replace("[databaseDriver]", database.getDriver());
                projectNameTagContent = projectNameTagContent.replace("[databaseSgbd]", database.getName());
                projectNameTagContent = projectNameTagContent.replace("[databaseHost]", credentials.getHost());
                projectNameTagContent = projectNameTagContent.replace("[databasePort]", database.getPort());
                projectNameTagContent = projectNameTagContent.replace("[databaseName]", credentials.getDatabaseName());
                projectNameTagContent = projectNameTagContent.replace("[user]", credentials.getUser());
                projectNameTagContent = projectNameTagContent.replace("[pwd]", credentials.getPwd());
                projectNameTagContent = projectNameTagContent.replace("[databaseUseSSL]", String.valueOf(credentials.isUseSSL()));
                projectNameTagContent = projectNameTagContent.replace("[databaseAllowKey]", String.valueOf(credentials.isAllowPublicKeyRetrieval()));
                projectNameTagContent = projectNameTagContent.replace("[databaseID]", String.valueOf(database.getId()));
                projectNameTagContent = projectNameTagContent.replace("[projectNameMin]", FileUtils.minStart(projectName));
                FileUtils.overwriteFileContent(projectNameTagPath, projectNameTagContent);
            }
            try (Connection connect = database.getConnexion(credentials)) {
                entities = database.getEntities(connect, credentials, entityName);
                GenesisObject obj = new GenesisObject();
                obj.setDatabaseId(database.getId());
                obj.setLanguageId(language.getId());
                obj.setProjectName(projectName);
                obj.setCredentials(credentials);
                obj.setEntities(entities);
                for (Entity entity : entities) {
                    entity.initialize(connect, credentials, database, language);
                }
                File genesisObj = new File(project, Constantes.GENESISOBJ_FILE);
                FileUtils.overwriteFileContent(genesisObj.getPath(), FileUtils.toJson(obj));
                models = new String[entities.length];
                controllers = new String[entities.length];
                views = new String[entities.length];
                navLink = new StringBuilder();
                for (int i = 0; i < models.length; i++) {
                    if (entities[i].getTableName().startsWith("genesis_")) {
                        continue;
                    }
                    models[i] = language.generateModel(entities[i], projectName);
                    controllers[i] = language.generateController(entities[i], database, credentials, projectName);
                    views[i] = language.generateView(entities[i], projectName);
                    modelFile = language.getModel().getModelSavePath().replace("[projectNameMaj]", FileUtils.majStart(projectName));
                    controllerFile = language.getController().getControllerSavepath().replace("[projectNameMaj]", FileUtils.majStart(projectName));
                    viewFile = language.getView().getViewSavePath().replace("[projectNameMaj]", FileUtils.majStart(projectName));
                    viewFile = viewFile.replace("[projectNameMin]", FileUtils.minStart(projectName));
                    viewFile = viewFile.replace("[classNameMaj]", FileUtils.majStart(entities[i].getClassName()));
                    viewFile = viewFile.replace("[classNameMin]", FileUtils.minStart(entities[i].getClassName()));
                    modelFile = modelFile.replace("[projectNameMin]", FileUtils.minStart(projectName));
                    controllerFile = controllerFile.replace("[projectNameMin]", FileUtils.minStart(projectName));
                    modelFile += "/" + FileUtils.majStart(entities[i].getClassName()) + "." + language.getModel().getModelExtension();
                    controllerFile += "/" + FileUtils.majStart(entities[i].getClassName()) + language.getController().getControllerNameSuffix() + "." + language.getController().getControllerExtension();
                    viewFile += "/" + language.getView().getViewName() + "." + language.getView().getViewExtension();
                    viewFile = viewFile.replace("[classNameMin]", FileUtils.minStart(entities[i].getClassName()));
                    FileUtils.createFile(modelFile);
                    for (CustomFile f : language.getModel().getModelAdditionnalFiles()) {
                        foreignContext = new StringBuilder();
                        for (EntityField ef : entities[i].getFields()) {
                            if (ef.isForeign()) {
                                foreignContext.append(language.getModel().getModelForeignContextAttr());
                                foreignContext = new StringBuilder(foreignContext.toString().replace("[classNameMaj]", FileUtils.majStart(ef.getType())));
                            }
                        }
                        customFile = f.getName().replace("[classNameMaj]", FileUtils.majStart(entities[i].getClassName()));
                        customFile = language.getModel().getModelSavePath().replace("[projectNameMaj]", FileUtils.majStart(projectName)) + "/" + customFile;
                        customFile = customFile.replace("[projectNameMin]", FileUtils.minStart(projectName));
                        customFileContent = FileUtils.getFileContent(Constantes.DATA_PATH + "/" + f.getContent()).replace("[classNameMaj]", FileUtils.majStart(entities[i].getClassName()));
                        customFileContent = customFileContent.replace("[projectNameMin]", FileUtils.minStart(projectName));
                        customFileContent = customFileContent.replace("[projectNameMaj]", FileUtils.majStart(projectName));
                        customFileContent = customFileContent.replace("[databaseHost]", credentials.getHost());
                        customFileContent = customFileContent.replace("[databaseName]", credentials.getDatabaseName());
                        customFileContent = customFileContent.replace("[user]", credentials.getUser());
                        customFileContent = customFileContent.replace("[pwd]", credentials.getPwd());
                        customFileContent = customFileContent.replace("[modelForeignContextAttr]", foreignContext.toString());
                        FileUtils.createFile(customFile);
                        FileUtils.overwriteFileContent(customFile, customFileContent);
                    }
                    FileUtils.createFile(controllerFile);
                    FileUtils.createFile(viewFile);
                    FileUtils.overwriteFileContent(modelFile, models[i]);
                    FileUtils.overwriteFileContent(controllerFile, controllers[i]);
                    FileUtils.overwriteFileContent(viewFile, views[i]);
                    navLink.append(language.getNavbarLinks().getLink());
                    navLink = new StringBuilder(navLink.toString().replace("[projectNameMaj]", FileUtils.majStart(projectName)));
                    navLink = new StringBuilder(navLink.toString().replace("[projectNameMin]", FileUtils.minStart(projectName)));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameMin]", FileUtils.minStart(entities[i].getClassName())));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameMaj]", FileUtils.majStart(entities[i].getClassName())));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameformattedMin]", FileUtils.minStart(FileUtils.formatReadable(entities[i].getClassName()).trim())));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameformattedMaj]", FileUtils.majStart(FileUtils.formatReadable(entities[i].getClassName()).trim())));
                }
                navLink.append("\n").append(language.getView().getViewCommentStart()).append("[navbarLinks]").append(language.getView().getViewCommentEnd());
                navLinkPath = language.getNavbarLinks().getPath().replace("[projectNameMaj]", FileUtils.majStart(projectName));
                navLinkPath = navLinkPath.replace("[projectNameMin]", FileUtils.minStart(projectName));
                FileUtils.overwriteFileContent(navLinkPath, FileUtils.getFileContent(navLinkPath).replace("[navbarLinks]", navLink.toString()));
                for (CustomChanges c : language.getCustomChanges()) {
                    customChanges = new StringBuilder();
                    for (Entity e : entities) {
                        if (e.getTableName().startsWith("genesis_")) {
                            continue;
                        }
                        customChanges.append(c.getChanges());
                        customChanges = new StringBuilder(customChanges.toString().replace("[classNameMaj]", FileUtils.majStart(e.getClassName())));
                        customChanges = new StringBuilder(customChanges.toString().replace("[classNameMin]", FileUtils.minStart(e.getClassName())));
                        customChanges = new StringBuilder(customChanges.toString().replace("[databaseHost]", credentials.getHost()));
                        customChanges = new StringBuilder(customChanges.toString().replace("[user]", credentials.getUser()));
                        customChanges = new StringBuilder(customChanges.toString().replace("[databaseName]", credentials.getDatabaseName()));
                        customChanges = new StringBuilder(customChanges.toString().replace("[pwd]", credentials.getPwd()));
                    }
                    if (!c.isWithEndComma()) {
                        customChanges = new StringBuilder(customChanges.substring(0, customChanges.length() - 1));
                    }
                    customChanges.append("\n//[customChanges]");
                    changesFile = c.getPath().replace("[projectNameMaj]", FileUtils.majStart(projectName));
                    FileUtils.overwriteFileContent(changesFile, FileUtils.getFileContent(changesFile).replace("[customChanges]", customChanges.toString()));
                }
                SQLRunner.execute(connect, database.getLoginScript());
                connect.commit();
            }
        }
*/

        try (Scanner scanner = new Scanner(System.in)) {
            ApplicationTypeHandler applicationTypeHandler = new ApplicationTypeHandler();
            ApplicationHandler applicationHandler = new ApplicationHandler();
            FrameworkHandler frameworkHandler = new FrameworkHandler();
            LanguageHandler languageHandler = new LanguageHandler();
            DatabaseHandler databaseHandler = new DatabaseHandler();
            ProjectSetup projectSetup = new ProjectSetup();

            /*---Generate codes for existing or new projects---*/
            Application application = applicationHandler.chooseApplication(scanner);

            /*---Enter information about the database you wish to use---*/
            Database database = databaseHandler.chooseDatabase(scanner);
            Credentials credentials = databaseHandler.inputCredentials(scanner);

            /*---Choosing the right application type for your project---*/
            ApplicationType applicationType = applicationTypeHandler.chooseTypeApplication(scanner);

            /*---Choosing the programming language---*/
            Language language = languageHandler.chooseLanguage(scanner, applicationType);

            /*---Choosing the framework---*/
            Framework framework = frameworkHandler.chooseFramework(scanner,applicationType,language);

            /*---Project configuration---*/
            String projectName = projectSetup.setupProject(scanner, framework);
        }
    }
}

