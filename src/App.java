import java.io.File;
import java.sql.Connection;
import java.util.Scanner;

import genesis.Constantes;
import genesis.Credentials;
import genesis.CustomChanges;
import genesis.CustomFile;
import genesis.Database;
import genesis.Entity;
import genesis.EntityField;
import genesis.GenesisObject;
import genesis.Language;
import handyman.HandyManUtils;
import veda.godao.DAO;
public class App {
    public static void main(String[] args) throws Exception {
        Database[] databases=HandyManUtils.fromJson(Database[].class, HandyManUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages=HandyManUtils.fromJson(Language[].class, HandyManUtils.getFileContent(Constantes.LANGUAGE_JSON));
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
        try(Scanner scanner=new Scanner(System.in)){
            System.out.println("Choose a database engine:");
            for(int i=0;i<databases.length;i++){
                System.out.println((i+1)+") "+databases[i].getNom());
            }
            System.out.print("> ");
            database=databases[scanner.nextInt()-1];
            System.out.println("Choose a framework:");
            for(int i=0;i<languages.length;i++){
                System.out.println((i+1)+") "+languages[i].getNom());
            }
            System.out.print("> ");
            language=languages[scanner.nextInt()-1];
            System.out.println("Enter your database credentials:");
            System.out.print("Database name: ");
            databaseName=scanner.next();
            System.out.print("Username: ");
            user=scanner.next();
            System.out.print("Password: ");
            pwd=scanner.next();
            System.out.print("Database host: ");
            host=scanner.next();
            System.out.print("Use SSL ?(Y/n): ");
            useSSL=scanner.next().equalsIgnoreCase("Y");
            System.out.print("Allow public key retrieval ?(Y/n): ");
            allowPublicKeyRetrieval=scanner.next().equalsIgnoreCase("Y");
            System.out.println();
            System.out.print("Enter your project name: ");
            projectName=scanner.next();
            System.out.print("Which entities to import ?(* to select all): ");
            entityName=scanner.next();
            credentials=new Credentials(databaseName, user, pwd, host, useSSL, allowPublicKeyRetrieval);
            project=new File(projectName);
            project.mkdir();
            for(CustomFile c:language.getAdditionnalFiles()){
                customFilePath=c.getName();
                customFilePath=customFilePath.replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                HandyManUtils.createFile(customFilePath);
                customFileContentOuter=HandyManUtils.getFileContent(Constantes.DATA_PATH+"/"+c.getContent()).replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                HandyManUtils.overwriteFileContent(customFilePath, customFileContentOuter);
            }
            HandyManUtils.extractDir(Constantes.DATA_PATH+"/"+language.getSkeleton()+"."+Constantes.SKELETON_EXTENSION, project.getPath());
            for(String replace:language.getProjectNameTags()){
                projectNameTagPath=replace.replace("[projectNameMaj]", HandyManUtils.majStart(projectName)).replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                projectNameTagContent=HandyManUtils.getFileContent(projectNameTagPath).replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                projectNameTagContent=projectNameTagContent.replace("[databaseDriver]", database.getDriver());
                projectNameTagContent=projectNameTagContent.replace("[databaseSgbd]", database.getNom());
                projectNameTagContent=projectNameTagContent.replace("[databaseHost]", credentials.getHost());
                projectNameTagContent=projectNameTagContent.replace("[databasePort]", database.getPort());
                projectNameTagContent=projectNameTagContent.replace("[databaseName]", credentials.getDatabaseName());
                projectNameTagContent=projectNameTagContent.replace("[user]", credentials.getUser());
                projectNameTagContent=projectNameTagContent.replace("[pwd]", credentials.getPwd());
                projectNameTagContent=projectNameTagContent.replace("[databaseUseSSL]", String.valueOf(credentials.isUseSSL()));
                projectNameTagContent=projectNameTagContent.replace("[databaseAllowKey]", String.valueOf(credentials.isAllowPublicKeyRetrieval()));
                projectNameTagContent=projectNameTagContent.replace("[databaseID]", String.valueOf(database.getId()));
                projectNameTagContent=projectNameTagContent.replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                HandyManUtils.overwriteFileContent(projectNameTagPath, projectNameTagContent);
            }
            try(Connection connect=database.getConnexion(credentials)){
                entities=database.getEntities(connect, credentials, entityName);
                GenesisObject obj=new GenesisObject();
                obj.setDatabaseId(database.getId());
                obj.setLanguageId(language.getId());
                obj.setProjectName(projectName);
                obj.setCredentials(credentials);
                obj.setEntities(entities);
                for(int i=0;i<entities.length;i++){
                    entities[i].initialize(connect, credentials, database, language);
                }
                File genesisObj=new File(project, Constantes.GENESISOBJ_FILE);
                HandyManUtils.overwriteFileContent(genesisObj.getPath(), HandyManUtils.toJson(obj));
                models=new String[entities.length];
                controllers=new String[entities.length];
                views=new String[entities.length];
                navLink = new StringBuilder();
                for(int i=0;i<models.length;i++){
                    if(entities[i].getTableName().startsWith("genesis_")){
                        continue;
                    }
                    models[i]=language.generateModel(entities[i], projectName);
                    controllers[i]=language.generateController(entities[i], database, credentials, projectName);
                    views[i]=language.generateView(entities[i], projectName);
                    modelFile=language.getModel().getModelSavePath().replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                    controllerFile=language.getController().getControllerSavepath().replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                    viewFile=language.getView().getViewSavePath().replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                    viewFile=viewFile.replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                    viewFile=viewFile.replace("[classNameMaj]", HandyManUtils.majStart(entities[i].getClassName()));
                    viewFile=viewFile.replace("[classNameMin]", HandyManUtils.minStart(entities[i].getClassName()));
                    modelFile=modelFile.replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                    controllerFile=controllerFile.replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                    modelFile+="/"+HandyManUtils.majStart(entities[i].getClassName())+"."+language.getModel().getModelExtension();
                    controllerFile+="/"+HandyManUtils.majStart(entities[i].getClassName())+language.getController().getControllerNameSuffix()+"."+language.getController().getControllerExtension();
                    viewFile+="/"+language.getView().getViewName()+"."+language.getView().getViewExtension();
                    viewFile=viewFile.replace("[classNameMin]", HandyManUtils.minStart(entities[i].getClassName()));
                    HandyManUtils.createFile(modelFile);
                    for(CustomFile f:language.getModel().getModelAdditionnalFiles()){
                        foreignContext=new StringBuilder();
                        for(EntityField ef:entities[i].getFields()){
                            if(ef.isForeign()){
                                foreignContext.append(language.getModel().getModelForeignContextAttr());
                                foreignContext = new StringBuilder(foreignContext.toString().replace("[classNameMaj]", HandyManUtils.majStart(ef.getType())));
                            }
                        }
                        customFile=f.getName().replace("[classNameMaj]", HandyManUtils.majStart(entities[i].getClassName()));
                        customFile=language.getModel().getModelSavePath().replace("[projectNameMaj]", HandyManUtils.majStart(projectName))+"/"+customFile;
                        customFile=customFile.replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                        customFileContent=HandyManUtils.getFileContent(Constantes.DATA_PATH+"/"+f.getContent()).replace("[classNameMaj]", HandyManUtils.majStart(entities[i].getClassName()));
                        customFileContent=customFileContent.replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                        customFileContent=customFileContent.replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                        customFileContent=customFileContent.replace("[databaseHost]", credentials.getHost());
                        customFileContent=customFileContent.replace("[databaseName]", credentials.getDatabaseName());
                        customFileContent=customFileContent.replace("[user]", credentials.getUser());
                        customFileContent=customFileContent.replace("[pwd]", credentials.getPwd());
                        customFileContent=customFileContent.replace("[modelForeignContextAttr]", foreignContext.toString());
                        HandyManUtils.createFile(customFile);
                        HandyManUtils.overwriteFileContent(customFile, customFileContent);
                    }
                    HandyManUtils.createFile(controllerFile);
                    HandyManUtils.createFile(viewFile);
                    HandyManUtils.overwriteFileContent(modelFile, models[i]);
                    HandyManUtils.overwriteFileContent(controllerFile, controllers[i]);
                    HandyManUtils.overwriteFileContent(viewFile, views[i]);
                    navLink.append(language.getNavbarLinks().getLink());
                    navLink = new StringBuilder(navLink.toString().replace("[projectNameMaj]", HandyManUtils.majStart(projectName)));
                    navLink = new StringBuilder(navLink.toString().replace("[projectNameMin]", HandyManUtils.minStart(projectName)));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameMin]", HandyManUtils.minStart(entities[i].getClassName())));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameMaj]", HandyManUtils.majStart(entities[i].getClassName())));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameformattedMin]", HandyManUtils.minStart(HandyManUtils.formatReadable(entities[i].getClassName()).trim())));
                    navLink = new StringBuilder(navLink.toString().replace("[classNameformattedMaj]", HandyManUtils.majStart(HandyManUtils.formatReadable(entities[i].getClassName()).trim())));
                }
                navLink.append("\n"+language.getView().getViewCommentStart()+"[navbarLinks]"+language.getView().getViewCommentEnd());
                navLinkPath=language.getNavbarLinks().getPath().replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                navLinkPath=navLinkPath.replace("[projectNameMin]", HandyManUtils.minStart(projectName));
                HandyManUtils.overwriteFileContent(navLinkPath, HandyManUtils.getFileContent(navLinkPath).replace("[navbarLinks]", navLink.toString()));
                for(CustomChanges c:language.getCustomChanges()){
                    customChanges = new StringBuilder();
                    for(Entity e:entities){
                        if(e.getTableName().startsWith("genesis_")){
                            continue;
                        }
                        customChanges.append(c.getChanges());
                        customChanges = new StringBuilder(customChanges.toString().replace("[classNameMaj]", HandyManUtils.majStart(e.getClassName())));
                        customChanges = new StringBuilder(customChanges.toString().replace("[classNameMin]", HandyManUtils.minStart(e.getClassName())));
                        customChanges = new StringBuilder(customChanges.toString().replace("[databaseHost]", credentials.getHost()));
                        customChanges = new StringBuilder(customChanges.toString().replace("[user]", credentials.getUser()));
                        customChanges = new StringBuilder(customChanges.toString().replace("[databaseName]", credentials.getDatabaseName()));
                        customChanges = new StringBuilder(customChanges.toString().replace("[pwd]", credentials.getPwd()));
                    }
                    if(c.isWithEndComma()==false){
                        customChanges = new StringBuilder(customChanges.substring(0, customChanges.length() - 1));
                    }
                    customChanges.append("\n//[customChanges]");
                    changesFile=c.getPath().replace("[projectNameMaj]", HandyManUtils.majStart(projectName));
                    HandyManUtils.overwriteFileContent(changesFile, HandyManUtils.getFileContent(changesFile).replace("[customChanges]", customChanges.toString()));
                }
                DAO dao=new DAO(database.getDriver(), database.getNom(), databaseName, host, database.getPort(), user, pwd, useSSL, allowPublicKeyRetrieval, database.getId());
                dao.execute(connect, database.getLoginScript());
                connect.commit();
            }
        }
    }
}
