package genesis.config.langage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class Framework {
    private int id;
    private int langageId;
    private String name;
    private String skeleton;
    private String[] projectNameTags;
    private CustomFile[] additionnalFiles;
    private Model model;
    private Controller controller;
    private View view;
    private CustomChanges[] customChanges;
    private NavbarLink navbarLinks;


    @Getter
    @Setter
    public static class CustomFile {
        private String name;
        private String content;
    }

    @Getter
    @Setter
    public static class Model {
        private String[] modelImports;
        private String modelExtends;
        private String[] modelAnnotations;
        private String[] modelPrimaryFieldAnnotations;
        private String[] modelForeignFieldAnnotations;
        private String[] modelFieldAnnotations;
        private String modelFieldCase;
        private String modelFieldContent;
        private String modelGetter;
        private String modelSetter;
        private String[] modelConstructors;
        private String modelSavePath;
        private String modelForeignContextAttr;
        private CustomFile[] modelAdditionnalFiles;
        private String modelExtension;
        private String modelPackage;
        private String modelTemplate;
    }

    @Getter
    @Setter
    public static class Controller {
        private String[] controllerImports;
        private String[] controllerAnnotations;
        private String controllerExtends;
        private String controllerName;
        private ControllerField[] controllerFields;
        private ControllerField controllerFieldsForeign;
        private Map<String, String> controllerChangeInstanciation;
        private Map<String, String> controllerWhereInstanciation;
        private Map<String, String> controllerForeignInstanciation;
        private String controllerForeignList;
        private String controllerForeignContextParam;
        private String controllerForeignContextInstanciation;
        private String[] controllerConstructors;
        private String controllerForeignInclude;
        private ControllerMethod[] controllerMethods;
        private String controllerSavepath;
        private String controllerExtension;
        private String controllerPackage;
        private String controllerTemplate;
        private String controllerNameSuffix;
    }

    @Getter
    @Setter
    public static class ControllerField {
        private String[] controllerFieldAnnotations;
        private String controllerFieldContent;
    }

    @Getter
    @Setter
    public static class ControllerMethod {
        private String[] controllerMethodAnnotations;
        private String controllerMethodParameter;
        private String controllerMethodContent;
    }

    @Getter
    @Setter
    public static class View {
        private String viewForeignList;
        private String viewTableHeader;
        private String foreignFieldGet;
        private String viewTableLine;
        private String viewUpdateFormForeignField;
        private Map<String, String> viewUpdateFormField;
        private String viewInsertFormForeignField;
        private Map<String, String> viewInsertFormField;
        private String viewName;
        private String viewContent;
        private String viewSavePath;
        private String viewExtension;
        private String viewCommentStart;
        private String viewCommentEnd;
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
}
