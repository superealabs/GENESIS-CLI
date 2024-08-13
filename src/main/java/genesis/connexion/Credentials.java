package genesis.connexion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class Credentials {
    private String databaseName, user, pwd, host;
    private boolean useSSL, allowPublicKeyRetrieval;
}
