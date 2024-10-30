package genesis.config.langage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Editor {
    private int id;
    private String name;
    private int langageId;
    private Layout layout;
    private Insert insert;
    private Update update;
    private String template;

    @Getter
    @Setter
    @ToString
    public static class Layout {
        private Menu menu;
        private String name;
        private Header header;
        private Footer footer;
        private Content content;
        private String destinationPath;
    }

    @Getter
    @Setter
    @ToString
    public static class Header {
        private String iconsLink;
        private String coresLink;
        private String themeLink;
        private String assetsLink;
        private String vendorsLink;
        private String helpersLink;
        private String viewAttribute;
    }

    @Getter
    @Setter
    @ToString
    public static class Menu {
        private String logo;
        private String aside;
        private String listLink;
        private String createLink;
    }

    @Getter
    @Setter
    @ToString
    public static class Content {
        private String callMenu;
        private String callContent;
    }

    @Getter
    @Setter
    @ToString
    public static class Footer {
        private String coresFooterLink;
        private String pagesFooterLink;
        private String mainsFooterLink;
        private String vendorsFooterLink;
    }

    @Getter
    @Setter
    @ToString
    public static class Insert {
        private List<Input> input;
    }

    @Getter
    @Setter
    @ToString
    public static class Update {
        private List<Input> input;
    }

    @Getter
    @Setter
    @ToString
    public static class Input {
        private String inputType;
        private String inputContent;
    }

}
