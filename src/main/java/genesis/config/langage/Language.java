package genesis.config.langage;

import lombok.*;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@ToString
public class Language {
    private int id;
    private String name;
    private List<Integer> applicationId;
    private Map<String, String> syntax;
    private Map<String, String> types;
    private Map<String, String> typeParsers;
}
