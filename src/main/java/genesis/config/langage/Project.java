package genesis.config.langage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Project {
    private int id;
    private String name;
    private List<ProjectFiles> projectFiles;
    private List<ProjectFolders> projectFolders;
    private List<ProjectFilesEdit> projectFilesEdits;

    @Getter
    @Setter
    @ToString
    public static class ProjectFilesEdit {
        private String content;
        private String fileType;
        private String fileName;
        private String extension;
        private String destinationPath;
    }

    @Getter
    @Setter
    @ToString
    public static class ProjectFiles {
        private String fileType;
        private String fileName;
        private String sourcePath;
        private String destinationPath;
    }

    @Getter
    @Setter
    @ToString
    public static class ProjectFolders {
        private String folderName;
        private String folderType;
        private String sourcePath;
        private String destinationPath;
    }

}
