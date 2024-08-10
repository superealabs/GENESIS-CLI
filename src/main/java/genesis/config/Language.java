package genesis.config;

import genesis.connexion.Database;
import genesis.model.Model;
import genesis.view.NavbarLink;
import genesis.view.View;
import genesis.controller.Controller;
import genesis.controller.ControllerField;
import genesis.controller.ControllerMethod;
import genesis.model.Entity;
import genesis.model.EntityField;
import lombok.Getter;
import lombok.Setter;
import utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@Setter
@Getter
public class Language {
    private int id;
    private String name;
    private List<Integer> applicationId;
    private HashMap<String, String> syntax;
    private HashMap<String, String> types, typeParsers;
    private String skeleton;
    private String[] projectNameTags;
    private CustomFile[] additionnalFiles;
    private Model model;
    private Controller controller;
    private View view;
    private CustomChanges[] customChanges;
    private NavbarLink navbarLinks;

    public String generateModel(Entity entity, String projectName) throws IOException {
        String content = FileUtils.getFileContent(Constantes.DATA_PATH + "/" + getModel().getModelTemplate() + "." + Constantes.MODEL_TEMPLATE_EXT);
        content = content.replace("[namespace]", getSyntax().get("namespace"));
        content = content.replace("[namespaceStart]", getSyntax().get("namespaceStart"));
        content = content.replace("[namespaceEnd]", getSyntax().get("namespaceEnd"));
        content = content.replace("[package]", getModel().getModelPackage());
        StringBuilder imports = new StringBuilder();
        for (String i : getModel().getModelImports()) {
            imports.append(i).append("\n");
        }
        content = content.replace("[imports]", imports.toString());
        StringBuilder annotes = new StringBuilder();
        for (String a : getModel().getModelAnnotations()) {
            annotes.append(a).append("\n");
        }
        content = content.replace("[classAnnotations]", annotes.toString());
        content = content.replace("[extends]", getModel().getModelExtends());
        StringBuilder constructors = new StringBuilder();
        for (String c : getModel().getModelConstructors()) {
            constructors.append(c).append("\n");
        }
        content = content.replace("[constructors]", constructors.toString());
        StringBuilder fields = new StringBuilder();
        StringBuilder fieldAnnotes;
        for (int i = 0; i < entity.getFields().length; i++) {
            fieldAnnotes = new StringBuilder();
            if (entity.getFields()[i].isPrimary()) {
                for (String primAnnote : getModel().getModelPrimaryFieldAnnotations()) {
                    fieldAnnotes.append(primAnnote).append("\n");
                }
            } else if (entity.getFields()[i].isForeign()) {
                for (String forAnnote : getModel().getModelForeignFieldAnnotations()) {
                    fieldAnnotes.append(forAnnote).append("\n");
                    fieldAnnotes = new StringBuilder(fieldAnnotes.toString().replace("[referencedFieldNameMin]", FileUtils.minStart(entity.getFields()[i].getReferencedField())));
                    fieldAnnotes = new StringBuilder(fieldAnnotes.toString().replace("[referencedFieldNameMaj]", FileUtils.majStart(entity.getFields()[i].getReferencedField())));
                }
            }
            for (String fa : getModel().getModelFieldAnnotations()) {
                fieldAnnotes.append(fa).append("\n");
            }
            fields.append(fieldAnnotes);
            fields.append(getModel().getModelFieldContent()).append("\n");
            fields.append(getModel().getModelGetter()).append("\n");
            fields.append(getModel().getModelSetter()).append("\n");
            fields = new StringBuilder(fields.toString().replace("[columnName]", entity.getColumns()[i].getName()));
            fields = new StringBuilder(fields.toString().replace("[fieldType]", entity.getFields()[i].getType()));
            fields = new StringBuilder(fields.toString().replace("[modelFieldCase]", getModel().getModelFieldCase()));
            fields = new StringBuilder(fields.toString().replace("[fieldNameMin]", FileUtils.minStart(entity.getFields()[i].getName())));
            fields = new StringBuilder(fields.toString().replace("[fieldNameMaj]", FileUtils.majStart(entity.getFields()[i].getName())));
        }
        content = content.replace("[fields]", fields.toString());
        content = content.replace("[projectNameMin]", FileUtils.minStart(projectName));
        content = content.replace("[projectNameMaj]", FileUtils.majStart(projectName));
        content = content.replace("[classNameMaj]", FileUtils.majStart(entity.getClassName()));
        content = content.replace("[modelFieldCase]", getModel().getModelFieldCase());
        content = content.replace("[primaryFieldType]", entity.getPrimaryField().getType());
        content = content.replace("[primaryFieldNameMin]", FileUtils.minStart(entity.getPrimaryField().getName()));
        content = content.replace("[primaryFieldNameMaj]", FileUtils.majStart(entity.getPrimaryField().getName()));
        content = content.replace("[tableName]", entity.getTableName());
        return content;
    }

    public String generateController(Entity entity, Database database, Credentials credentials, String projectName) throws IOException {
        String content = FileUtils.getFileContent(Constantes.DATA_PATH + "/" + getController().getControllerTemplate() + "." + Constantes.CONTROLLER_TEMPLATE_EXT);
        content = content.replace("[namespace]", getSyntax().get("namespace"));
        content = content.replace("[namespaceStart]", getSyntax().get("namespaceStart"));
        content = content.replace("[namespaceEnd]", getSyntax().get("namespaceEnd"));
        content = content.replace("[package]", getController().getControllerPackage());
        StringBuilder imports = new StringBuilder();
        for (String i : getController().getControllerImports()) {
            imports.append(i).append("\n");
        }
        content = content.replace("[imports]", imports.toString());
        StringBuilder annotes = new StringBuilder();
        for (String a : getController().getControllerAnnotations()) {
            annotes.append(a).append("\n");
        }
        content = content.replace("[controllerAnnotations]", annotes.toString());
        content = content.replace("[extends]", getController().getControllerExtends());
        StringBuilder fields = new StringBuilder();
        StringBuilder fieldAnnotes;
        for (ControllerField cf : getController().getControllerFields()) {
            fieldAnnotes = new StringBuilder();
            for (String a : cf.getControllerFieldAnnotations()) {
                fieldAnnotes.append(a).append("\n");
            }
            fields.append(fieldAnnotes);
            fields.append(cf.getControllerFieldContent()).append("\n");
        }
        for (EntityField ef : entity.getFields()) {
            if (ef.isForeign() && getController().getControllerFieldsForeign() != null) {
                fieldAnnotes = new StringBuilder();
                for (String a : getController().getControllerFieldsForeign().getControllerFieldAnnotations()) {
                    fieldAnnotes.append(a).append("\n");
                }
                fields.append(fieldAnnotes);
                fields.append(getController().getControllerFieldsForeign().getControllerFieldContent()).append("\n");
                fields = new StringBuilder(fields.toString().replace("[foreignNameMaj]", FileUtils.majStart(ef.getType())));
                fields = new StringBuilder(fields.toString().replace("[foreignNameMin]", FileUtils.minStart(ef.getType())));
            }
        }
        content = content.replace("[fields]", fields.toString());
        StringBuilder constructors = new StringBuilder();
        StringBuilder constructorParameter;
        StringBuilder foreignInstanciation;
        for (String c : getController().getControllerConstructors()) {
            constructorParameter = new StringBuilder();
            foreignInstanciation = new StringBuilder();
            for (EntityField ef : entity.getFields()) {
                if (ef.isForeign()) {
                    constructorParameter.append(",").append(getController().getControllerForeignContextParam());
                    constructorParameter = new StringBuilder(constructorParameter.toString().replace("[foreignNameMaj]", FileUtils.majStart(ef.getName())));
                    foreignInstanciation.append(getController().getControllerForeignContextInstanciation());
                    foreignInstanciation = new StringBuilder(foreignInstanciation.toString().replace("[foreignNameMaj]", FileUtils.majStart(ef.getName())) + "\n");
                }
            }
            constructors.append(c).append("\n");
            constructors = new StringBuilder(constructors.toString().replace("[controllerForeignContextParam]", constructorParameter.toString()));
            constructors = new StringBuilder(constructors.toString().replace("[controllerForeignContextInstanciation]", foreignInstanciation.toString()));
        }
        content = content.replace("[constructors]", constructors.toString());
        StringBuilder methods = new StringBuilder();
        StringBuilder methodAnnotes;
        StringBuilder methodParameters;
        StringBuilder changeInstanciation;
        String whereInstanciation;
        StringBuilder foreignList;
        StringBuilder foreignInclude;
        for (ControllerMethod m : getController().getControllerMethods()) {
            methodAnnotes = new StringBuilder();
            for (String ma : m.getControllerMethodAnnotations()) {
                methodAnnotes.append(ma).append("\n");
            }
            methods.append(methodAnnotes);
            methodParameters = new StringBuilder();
            for (EntityField ef : entity.getFields()) {
                methodParameters.append(m.getControllerMethodParameter());
                if (!(methodParameters.isEmpty())) {
                    methodParameters.append(",");
                }
                methodParameters = new StringBuilder(methodParameters.toString().replace("[fieldType]", ef.getType()));
                methodParameters = new StringBuilder(methodParameters.toString().replace("[fieldNameMin]", FileUtils.minStart(ef.getName())));
            }
            if (methodParameters.isEmpty() == false) {
                methodParameters = new StringBuilder(methodParameters.substring(0, methodParameters.length() - 1));
            }
            methods.append(FileUtils.getFileContent(Constantes.DATA_PATH + "/" + m.getControllerMethodContent() + "." + Constantes.CONTROLLER_TEMPLATE_EXT));
            methods = new StringBuilder(methods.toString().replace("[controllerMethodParameter]", methodParameters.toString()));
            changeInstanciation = new StringBuilder();
            foreignList = new StringBuilder();
            foreignInclude = new StringBuilder();
            for (int i = 0; i < entity.getFields().length; i++) {
                if (entity.getFields()[i].isPrimary()) {
                    continue;
                } else if (entity.getFields()[i].isForeign()) {
                    changeInstanciation.append(getController().getControllerForeignInstanciation().get("template"));
                    changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[content]", getTypeParsers().get(getTypes().get(database.getTypes().get(entity.getColumns()[i].getType())))));
                    changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[value]", getController().getControllerForeignInstanciation().get("value")));
                    changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[fieldNameMin]", FileUtils.minStart(entity.getFields()[i].getName())));
                    changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[fieldNameMaj]", FileUtils.majStart(entity.getFields()[i].getName())));
                    changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[foreignType]", entity.getFields()[i].getType()));
                    changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[referencedFieldNameMaj]", FileUtils.majStart(entity.getFields()[i].getReferencedField())));
                    changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[foreignNameMin]", FileUtils.minStart(entity.getFields()[i].getName())));
                    foreignList.append(getController().getControllerForeignList());
                    foreignList = new StringBuilder(foreignList.toString().replace("[foreignType]", entity.getFields()[i].getType()));
                    foreignList = new StringBuilder(foreignList.toString().replace("[foreignNameMin]", FileUtils.minStart(entity.getFields()[i].getName())));
                    foreignInclude.append(getController().getControllerForeignInclude());
                    foreignInclude = new StringBuilder(foreignInclude.toString().replace("[foreignNameMaj]", FileUtils.majStart(entity.getFields()[i].getName())));
                    continue;
                }
                changeInstanciation.append(getController().getControllerChangeInstanciation().get("template"));
                changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[content]", getTypeParsers().get(entity.getFields()[i].getType())));
                changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[value]", getController().getControllerChangeInstanciation().get("value")));
                changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[fieldNameMin]", FileUtils.minStart(entity.getFields()[i].getName())));
                changeInstanciation = new StringBuilder(changeInstanciation.toString().replace("[fieldNameMaj]", FileUtils.majStart(entity.getFields()[i].getName())));
            }
            whereInstanciation = "";
            whereInstanciation += getController().getControllerWhereInstanciation().get("template");
            whereInstanciation = whereInstanciation.replace("[content]", getTypeParsers().get(entity.getPrimaryField().getType()));
            whereInstanciation = whereInstanciation.replace("[value]", getController().getControllerWhereInstanciation().get("value"));
            methods = new StringBuilder(methods.toString().replace("[primaryParse]", getTypeParsers().get(entity.getPrimaryField().getType()).replace("[value]", "[primaryNameMin]")));
            methods = new StringBuilder(methods.toString().replace("[controllerChangeInstanciation]", changeInstanciation.toString()));
            methods = new StringBuilder(methods.toString().replace("[controllerWhereInstanciation]", whereInstanciation));
            methods = new StringBuilder(methods.toString().replace("[controllerForeignList]", foreignList.toString()));
            methods = new StringBuilder(methods.toString().replace("[controllerForeignInclude]", foreignInclude.toString()));
            methods = new StringBuilder(methods.toString().replace("[classNameMin]", FileUtils.minStart(entity.getClassName())));
            methods = new StringBuilder(methods.toString().replace("[classNameMaj]", FileUtils.majStart(entity.getClassName())));
            methods = new StringBuilder(methods.toString().replace("[primaryNameMaj]", FileUtils.majStart(entity.getPrimaryField().getName())));
            methods = new StringBuilder(methods.toString().replace("[primaryType]", entity.getPrimaryField().getType()));
            methods = new StringBuilder(methods.toString().replace("[primaryNameMin]", FileUtils.minStart(entity.getPrimaryField().getName())));
            methods = new StringBuilder(methods.toString().replace("[databaseDriver]", database.getDriver()));
            methods = new StringBuilder(methods.toString().replace("[databaseSgbd]", database.getName()));
            methods = new StringBuilder(methods.toString().replace("[databaseHost]", credentials.getHost()));
            methods = new StringBuilder(methods.toString().replace("[databasePort]", database.getPort()));
            methods = new StringBuilder(methods.toString().replace("[databaseName]", credentials.getDatabaseName()));
            methods = new StringBuilder(methods.toString().replace("[user]", credentials.getUser()));
            methods = new StringBuilder(methods.toString().replace("[pwd]", credentials.getPwd()));
            methods = new StringBuilder(methods.toString().replace("[databaseUseSSL]", String.valueOf(credentials.isUseSSL())));
            methods = new StringBuilder(methods.toString().replace("[databaseAllowKey]", String.valueOf(credentials.isAllowPublicKeyRetrieval())));
        }
        content = content.replace("[methods]", methods.toString());
        content = content.replace("[controllerNameMaj]", getController().getControllerName());
        content = content.replace("[classNameMaj]", FileUtils.majStart(entity.getClassName()));
        content = content.replace("[classNameMin]", FileUtils.minStart(entity.getClassName()));
        content = content.replace("[projectNameMin]", FileUtils.minStart(projectName));
        content = content.replace("[projectNameMaj]", FileUtils.majStart(projectName));
        content = content.replace("[databaseDriver]", database.getDriver());
        content = content.replace("[databaseSgbd]", database.getName());
        content = content.replace("[databaseHost]", credentials.getHost());
        content = content.replace("[databaseName]", credentials.getDatabaseName());
        content = content.replace("[databasePort]", database.getPort());
        content = content.replace("[databaseID]", String.valueOf(database.getId()));
        content = content.replace("[user]", credentials.getUser());
        content = content.replace("[pwd]", credentials.getPwd());
        content = content.replace("[databaseUseSSL]", String.valueOf(credentials.isUseSSL()));
        content = content.replace("[databaseAllowKey]", String.valueOf(credentials.isAllowPublicKeyRetrieval()));
        return content;
    }

    public String generateView(Entity entity, String projectName) throws IOException {
        String content = FileUtils.getFileContent(Constantes.DATA_PATH + "/" + getView().getViewContent() + "." + Constantes.VIEW_TEMPLATE_EXT);
        StringBuilder foreignList = new StringBuilder();
        StringBuilder tableHeader = new StringBuilder();
        StringBuilder tableLine = new StringBuilder();
        String foreignGet;
        StringBuilder updateForm = new StringBuilder();
        StringBuilder insertForm = new StringBuilder();
        for (EntityField ef : entity.getFields()) {
            foreignGet = "";
            tableHeader.append(getView().getViewTableHeader());
            tableHeader = new StringBuilder(tableHeader.toString().replace("[fieldNameFormattedMaj]", FileUtils.formatReadable(ef.getName())));
            tableHeader = new StringBuilder(tableHeader.toString().replace("[fieldNameMaj]", FileUtils.majStart(ef.getName())));
            tableHeader = new StringBuilder(tableHeader.toString().replace("[fieldNameMin]", FileUtils.minStart(ef.getName())));
            tableLine.append(getView().getViewTableLine());
            tableLine = new StringBuilder(tableLine.toString().replace("[fieldNameMaj]", FileUtils.majStart(ef.getName())));
            tableLine = new StringBuilder(tableLine.toString().replace("[fieldNameMin]", FileUtils.minStart(ef.getName())));
            if (ef.isPrimary()) {
                tableLine = new StringBuilder(tableLine.toString().replace("[foreignFieldGet]", foreignGet));
                continue;
            }
            if (ef.isForeign() == false) {
                updateForm.append(FileUtils.getFileContent(Constantes.DATA_PATH + "/" + getView().getViewUpdateFormField().get(ef.getType()) + "." + Constantes.VIEW_TEMPLATE_EXT));
                updateForm = new StringBuilder(updateForm.toString().replace("[fieldNameMin]", FileUtils.minStart(ef.getName())));
                updateForm = new StringBuilder(updateForm.toString().replace("[fieldNameFormattedMaj]", FileUtils.formatReadable(ef.getName())));
                updateForm = new StringBuilder(updateForm.toString().replace("[fieldNameMaj]", FileUtils.majStart(ef.getName())));
                insertForm.append(FileUtils.getFileContent(Constantes.DATA_PATH + "/" + getView().getViewInsertFormField().get(ef.getType()) + "." + Constantes.VIEW_TEMPLATE_EXT));
                insertForm = new StringBuilder(insertForm.toString().replace("[fieldNameMin]", FileUtils.minStart(ef.getName())));
                insertForm = new StringBuilder(insertForm.toString().replace("[fieldNameMaj]", FileUtils.majStart(ef.getName())));
                insertForm = new StringBuilder(insertForm.toString().replace("[fieldNameFormattedMaj]", FileUtils.formatReadable(ef.getName())));
                tableLine = new StringBuilder(tableLine.toString().replace("[foreignFieldGet]", foreignGet));
                continue;
            }
            updateForm.append(FileUtils.getFileContent(Constantes.DATA_PATH + "/" + getView().getViewUpdateFormForeignField() + "." + Constantes.VIEW_TEMPLATE_EXT));
            updateForm = new StringBuilder(updateForm.toString().replace("[foreignType]", ef.getType()));
            updateForm = new StringBuilder(updateForm.toString().replace("[foreignNameMin]", FileUtils.minStart(ef.getName())));
            updateForm = new StringBuilder(updateForm.toString().replace("[foreignNameMaj]", FileUtils.majStart(ef.getName())));
            updateForm = new StringBuilder(updateForm.toString().replace("[foreignPrimaryNameMaj]", FileUtils.majStart(ef.getReferencedField())));
            updateForm = new StringBuilder(updateForm.toString().replace("[foreignPrimaryNameMin]", FileUtils.minStart(ef.getReferencedField())));
            updateForm = new StringBuilder(updateForm.toString().replace("[fieldNameMaj]", FileUtils.majStart(ef.getName())));
            updateForm = new StringBuilder(updateForm.toString().replace("[fieldNameMin]", FileUtils.minStart(ef.getName())));
            updateForm = new StringBuilder(updateForm.toString().replace("[foreignNameFormattedMaj]", FileUtils.formatReadable(ef.getName())));
            insertForm.append(FileUtils.getFileContent(Constantes.DATA_PATH + "/" + getView().getViewInsertFormForeignField() + "." + Constantes.VIEW_TEMPLATE_EXT));
            insertForm = new StringBuilder(insertForm.toString().replace("[foreignType]", ef.getType()));
            insertForm = new StringBuilder(insertForm.toString().replace("[foreignNameMin]", FileUtils.minStart(ef.getName())));
            insertForm = new StringBuilder(insertForm.toString().replace("[foreignNameMaj]", FileUtils.majStart(ef.getName())));
            insertForm = new StringBuilder(insertForm.toString().replace("[foreignPrimaryNameMaj]", FileUtils.majStart(ef.getReferencedField())));
            insertForm = new StringBuilder(insertForm.toString().replace("[foreignPrimaryNameMin]", FileUtils.minStart(ef.getReferencedField())));
            insertForm = new StringBuilder(insertForm.toString().replace("[fieldNameMin]", FileUtils.minStart(ef.getName())));
            insertForm = new StringBuilder(insertForm.toString().replace("[fieldNameMaj]", FileUtils.majStart(ef.getName())));
            insertForm = new StringBuilder(insertForm.toString().replace("[foreignNameFormattedMaj]", FileUtils.formatReadable(ef.getName())));
            foreignGet = getView().getForeignFieldGet();
            tableLine = new StringBuilder(tableLine.toString().replace("[foreignFieldGet]", foreignGet));
            foreignList.append(getView().getViewForeignList());
            foreignList = new StringBuilder(foreignList.toString().replace("[foreignType]", ef.getType()));
            foreignList = new StringBuilder(foreignList.toString().replace("[foreignNameMin]", FileUtils.minStart(ef.getName())));
            foreignList = new StringBuilder(foreignList.toString().replace("[foreignNameMaj]", FileUtils.majStart(ef.getName())));
        }
        content = content.replace("[viewForeignList]", foreignList.toString());
        content = content.replace("[viewTableHeader]", tableHeader.toString());
        content = content.replace("[viewTableLine]", tableLine.toString());
        content = content.replace("[viewUpdateFormField]", updateForm.toString());
        content = content.replace("[viewInsertFormField]", insertForm.toString());
        content = content.replace("[projectNameMin]", FileUtils.minStart(projectName));
        content = content.replace("[projectNameMaj]", FileUtils.majStart(projectName));
        content = content.replace("[classNameMaj]", FileUtils.majStart(entity.getClassName()));
        content = content.replace("[classNameMin]", FileUtils.minStart(entity.getClassName()));
        content = content.replace("[primaryNameMaj]", FileUtils.majStart(entity.getPrimaryField().getName()));
        content = content.replace("[primaryNameMin]", FileUtils.minStart(entity.getPrimaryField().getName()));
        return content;
    }
}
