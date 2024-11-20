import handler.ProjectGeneratorHandler;

public class GenesisApiCLI {
    public static void main(String[] args) {
        ProjectGeneratorHandler projectGeneratorHandler = new ProjectGeneratorHandler();
        projectGeneratorHandler.generateProject();
    }
}

