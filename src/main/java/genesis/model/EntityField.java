package genesis.model;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;

@Setter
@Getter
public class EntityField {
    private String name;
    private String type;
    private boolean primary;
    private boolean foreign;
    private String referencedField;
}
