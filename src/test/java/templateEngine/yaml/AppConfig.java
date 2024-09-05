package templateEngine.yaml;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AppConfig {
    private Info info;

    @JsonProperty("info")
    public Info getInfo() { return info; }
    public void setInfo(Info info) { this.info = info; }
}

class Info {
    private String nom;
    private String version;
    private String description;

    // Constructeurs, getters et setters
    public Info() {}

    @JsonProperty("nom")
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    @JsonProperty("version")
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    @JsonProperty("description")
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
