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
    private Controller controller;
    private NavbarLink navbarLinks;
    private String[] projectNameTags;
    private CustomFile[] additionnalFiles;
    private CustomChanges[] customChanges;

    @Getter
    @Setter
    public static class Model {
        private ModelDao modelDao;
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
        private String name;
        private String content;
        private Boolean isUnique;
        private String packagePath;
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
