package genesis;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Credentials {
    private String databaseName, user, pwd, host;
    private boolean useSSL, allowPublicKeyRetrieval;

    public Credentials(String databaseName, String user, String pwd, String host, boolean useSSL,
                       boolean allowPublicKeyRetrieval) {
        this.databaseName = databaseName;
        this.user = user;
        this.pwd = pwd;
        this.host = host;
        this.useSSL = useSSL;
        this.allowPublicKeyRetrieval = allowPublicKeyRetrieval;
    }


}
