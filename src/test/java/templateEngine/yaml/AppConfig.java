package templateEngine.yaml;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class AppConfig {
    private Info info;

    @JsonProperty("info")
    public Info getInfo() {
        return info;
    }

}

@Setter
class Info {
    private String nom;
    private String version;
    private String description;

    // Constructeurs, getters et setters
    public Info() {
    }

    @JsonProperty("nom")
    public String getNom() {
        return nom;
    }

    @JsonProperty("version")
    public String getVersion() {
        return version;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

}
