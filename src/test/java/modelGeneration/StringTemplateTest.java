package modelGeneration;

import org.junit.jupiter.api.Test;

public class StringTemplateTest {
    @Test
    void strTemplate() {
        /*String name = "Alice";
        String greeting = STR."""
                            Hello, \{name}!
                            Comment ca va ?
                            En forme ???
                                        OK
        """;
        // Résultat : "Hello, Alice!"
        System.out.println(greeting);

        int x = 10;
        int y = 20;
        String result = STR."\{x} + \{y} = \{x + y}";
        System.out.println(result);
         */
    }


    @Test
    void name() {
        String template = """
            [namespace] [package][namespaceStart]
            [imports]
            [classAnnotations]
            public class [classNameMaj] [extends] {
                [fields]
                [constructors]
            }
            [namespaceEnd]
        """;

       template = template.replace("[namespace]", "package")
                .replace("[namespaceStart]","")
                .replace("[namespaceEnd]","")
                .replace("[package]","test.com")
                .replace("[imports]","import test;")
                .replace("[classAnnotations]","@Table")
                .replace("[extends]","extends Table")
                .replace("[projectNameMin]","project")
                .replace("[projectNameMaj]","Project")
                .replace("[tableName]","Project")
                .replace("[classNameMaj]","Project");

       System.out.println(template);
    }
}
