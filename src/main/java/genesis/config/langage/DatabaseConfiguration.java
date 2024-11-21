package genesis.config.langage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DatabaseConfiguration {
    private String port;
    private String user;
    private String host;
    private String name;
    private String password;
}
