package genesis.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Application {
    private int id;
    private String nom;
    private boolean trueApplication;

    public Application() {
    }

    public Application(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public Application(int id, String nom, boolean trueApplication) {
        this.id = id;
        this.nom = nom;
        this.trueApplication = trueApplication;
    }
}
