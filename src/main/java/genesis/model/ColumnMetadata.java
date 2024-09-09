package genesis.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ColumnMetadata {
    private String name;
    private String type;
    private boolean primary;
    private boolean foreign;
    private String referencedTable;
    private String referencedColumn;
}
