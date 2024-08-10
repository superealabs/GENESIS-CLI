package genesis.controller;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Getter
@Setter
public class Controller {
    private String[] controllerImports;
    private String[] controllerAnnotations;
    private String controllerExtends;
    private String controllerName;
    private ControllerField[] controllerFields;
    private ControllerField controllerFieldsForeign;
    private HashMap<String, String> controllerChangeInstanciation, controllerWhereInstanciation, controllerForeignInstanciation;
    private String controllerForeignList;
    private String controllerForeignContextParam, controllerForeignContextInstanciation;
    private String[] controllerConstructors;
    private String controllerForeignInclude;
    private ControllerMethod[] controllerMethods;
    private String controllerSavepath;
    private String controllerExtension;
    private String controllerPackage, controllerTemplate, controllerNameSuffix;

}
