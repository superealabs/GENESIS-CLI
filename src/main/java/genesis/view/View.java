package genesis.view;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Setter
@Getter
public class View {
    private String viewForeignList;
    private String viewTableHeader;
    private String foreignFieldGet;
    private String viewTableLine;
    private String viewUpdateFormForeignField;
    private HashMap<String, String> viewUpdateFormField;
    private String viewInsertFormForeignField;
    private HashMap<String, String> viewInsertFormField;
    private String viewName, viewContent;
    private String viewSavePath;
    private String viewExtension;
    private String viewCommentStart, viewCommentEnd;

}
