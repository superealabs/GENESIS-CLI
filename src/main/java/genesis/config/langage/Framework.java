package genesis.config.langage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

import genesis.config.CustomFile;

@Getter
@Setter
@ToString
public class Framework {
    private int id;
    private View view;
    private String name;
    private Model model;
    private int langageId;
    private String skeleton;
    private String template;
    private Service service;
    private ModelDao modelDao;
    private Controller controller;
    private NavbarLink navbarLinks;
    private String[] projectNameTags;
    private Dependency[] dependencies;
    private CustomFile[] additionalFiles;
    private CustomChanges[] customChanges;

    @Getter
    @Setter
    public static class Model {
        private String modelImports;
        private String modelExtends;
        private String modelPackage;
        private String modelSavePath;
        private String modelExtension;
        private String modelAnnotations;
        private String modelFieldContent;
        private String modelGetterSetter;
        private String modelConstructors;
        private String modelForeignContextAttr;
    }

    @Getter
    @Setter
    public static class ModelDao {
        private String isUnique;
        private String modelDaoName;
        private String modelDaoImports;
        private String modelDaoExtends;
        private String modelDaoPackage;
        private String modelDaoSavePath;
        private String modelDaoExtension;
        private String modelDaoAnnotations;
        private String modelDaoFieldContent;
        private String modelDaoConstructors;
        private String modelDaoMethodContent;
    }

    @Getter
    @Setter
    public static class Service {
        private String serviceName;
        private String serviceImports;
        private String serviceExtends;
        private String servicePackage;
        private String serviceSavePath;
        private String serviceExtension;
        private String serviceAnnotations;
        private String serviceFieldContent;
        private String serviceConstructors;
        private String serviceMethodContent;
    }

    @Getter
    @Setter
    public static class Controller {
        private String controllerName;
        private String controllerImports;
        private String controllerExtends;
        private String controllerPackage;
        private String controllerSavePath;
        private String controllerExtension;
        private String controllerAnnotations;
        private String controllerFieldContent;
        private String controllerConstructors;
        private String controllerMethodContent;
    }

    @Getter
    @Setter
    public static class View {
        private String viewSavePath;
        private String listViewName;
        private String viewExtension;
        private String createViewName;
    }

    @Getter
    @Setter
    public static class CustomChanges {
        private String path;
        private String changes;
        private boolean withEndComma;
    }

    @Getter
    @Setter
    public static class NavbarLink {
        private String path;
        private String link;
    }

    @Getter
    @Setter
    public static class Dependency {
        private String groupId;
        private String version;
        private String artifactId;
    }
}
