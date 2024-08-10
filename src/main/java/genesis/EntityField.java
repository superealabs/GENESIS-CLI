package genesis;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EntityField {
    private String name;
    private String type;
    private boolean primary;
    private boolean foreign;
    private String referencedField;

}
