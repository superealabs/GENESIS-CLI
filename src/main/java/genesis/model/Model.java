package genesis.model;

import genesis.config.CustomFile;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Model {
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
