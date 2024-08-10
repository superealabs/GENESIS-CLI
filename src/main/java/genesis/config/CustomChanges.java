package genesis.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomChanges {
    private String path;
    private String changes;
    private boolean withEndComma;

}
