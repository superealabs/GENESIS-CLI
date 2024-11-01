package genesis.config.langage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Framework {
    private int id;
    private int languageId;
    private String name;
    private String template;
    private Boolean useDB;
    private Boolean withGroupId;
    private List<ConfigurationMetadata> configurations;
    private List<Dependency> dependencies;
    private Model model;
    private ModelDao modelDao;
    private Service service;
    private Controller controller;

    @Getter
    @Setter
    @ToString
    public static class Dependency {
        private String groupId;
        private String artifactId;
        private String version;
    }

    @Getter
    @Setter
    @ToString
    public static class Model {
        private String modelImports;
        private String modelExtends;
        private String modelAnnotations;
        private String modelFieldContent;
        private String modelGetterSetter;
        private String modelConstructors;
        private String modelSavePath;
        private String modelForeignContextAttribute;
        private String modelPackage;
    }

    @Getter
    @Setter
    @ToString
    public static class ModelDao {
        private String modelDaoImports;
        private String modelDaoAnnotations;
        private String modelDaoClassKeyword;
        private String modelDaoExtends;
        private String modelDaoName;
        private String modelDaoFieldContent;
        private String modelDaoMethodContent;
        private String modelDaoConstructors;
        private String modelDaoSavePath;
        private String modelDaoPackage;
        private List<Project.ProjectFilesEdit> modelDaoAdditionalFiles;
    }

    @Getter
    @Setter
    @ToString
    public static class Service {
        private String serviceImports;
        private String serviceClassKeyword;
        private String serviceAnnotations;
        private String serviceExtends;
        private String serviceName;
        private String serviceFieldContent;
        private String serviceConstructors;
        private String serviceMethodContent;
        private String serviceSavePath;
        private String servicePackage;
        private List<Project.ProjectFilesEdit> serviceAdditionalFiles;
    }

    @Getter
    @Setter
    @ToString
    public static class Controller {
        private String controllerImports;
        private String controllerAnnotations;
        private String controllerExtends;
        private String controllerName;
        private String controllerFieldContent;
        private String controllerConstructors;
        private String controllerMethodContent;
        private String controllerSavePath;
        private String controllerPackage;
        private List<Project.ProjectFilesEdit> controllerAdditionalFiles;
    }
}