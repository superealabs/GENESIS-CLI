package genesis;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GenesisObject {
    private String projectName;
    private int databaseId;
    private int languageId;
    private Credentials credentials;
    private Entity[] entities;

}
