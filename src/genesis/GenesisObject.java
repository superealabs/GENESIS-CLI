package genesis;

public class GenesisObject {
    private String projectName;
    private int databaseId;
    private int languageId;
    private Credentials credentials;
    private Entity[] entities;
    public Entity[] getEntities() {
        return entities;
    }
    public void setEntities(Entity[] entities) {
        this.entities = entities;
    }
    public Credentials getCredentials() {
        return credentials;
    }
    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    public int getDatabaseId() {
        return databaseId;
    }
    public void setDatabaseId(int databaseId) {
        this.databaseId = databaseId;
    }
    public int getLanguageId() {
        return languageId;
    }
    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }
    
}
