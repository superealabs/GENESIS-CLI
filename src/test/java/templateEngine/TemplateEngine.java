package templateEngine;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TemplateEngine {

    // Static constants for templating keywords
    private static final String LOOP_START = "{{#each ";
    private static final String LOOP_END = "{{/each}}";
    private static final String IF_START = "{{#if ";
    private static final String ELSE_TOKEN = "{{else}}";
    private static final String IF_END = "{{/if}}";
    private static final String VARIABLE_PLACEHOLDER_PREFIX = "${";
    private static final String VARIABLE_PLACEHOLDER_SUFFIX = "}";
    private static final String NEWLINE_TAG = "{{newline}}";
    private static final String TAB_TAG = "{{tab}}";

    // Map to hold available functions
    private static final Map<String, Function<String, String>> FUNCTIONS_MAP = new HashMap<>();

    static {
        FUNCTIONS_MAP.put("upperCase", String::toUpperCase);
        FUNCTIONS_MAP.put("lowerCase", String::toLowerCase);
        FUNCTIONS_MAP.put("majStart", str -> str.substring(0, 1).toUpperCase() + str.substring(1));
    }

    public String render(String template, HashMap<String, Object> variables) throws Exception {
        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("The template must not be empty.");
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        StringBuilder result = new StringBuilder(template);

        // Evaluate loops
        evaluateLoops(result, variables);

        // Evaluate if-else blocks
        evaluateConditionals(result, variables);

        // Replace variables and evaluate function calls
        replaceVariables(result, variables);

        // Process special tags like {{newline}} and {{tab}}
        processSpecialTags(result);

        // Check if there are undefined variables left in the template
        checkUndefinedVariables(result);

        return result.toString();
    }

    private void evaluateLoops(StringBuilder template, HashMap<String, Object> variables) throws Exception {
        int start;
        while ((start = template.indexOf(LOOP_START)) != -1) {
            LoopInfo loopInfo = extractLoopInfo(template, start);
            processLoopContent(template, loopInfo, variables);
        }
    }

    private LoopInfo extractLoopInfo(StringBuilder template, int start) throws Exception {
        int loopEndIdx = template.indexOf(LOOP_END, start);
        if (loopEndIdx == -1) {
            throw new Exception("Loop end tag not found.");
        }

        int loopVarStartIdx = start + LOOP_START.length();
        int loopVarEndIdx = template.indexOf("}}", loopVarStartIdx);
        String loopVarName = template.substring(loopVarStartIdx, loopVarEndIdx).trim();

        int contentStartIdx = loopVarEndIdx + 2;
        String loopContent = template.substring(contentStartIdx, loopEndIdx);

        template.delete(start, loopEndIdx + LOOP_END.length());

        return new LoopInfo(loopVarName, loopContent, start);
    }

    private void processLoopContent(StringBuilder template, LoopInfo loopInfo, HashMap<String, Object> variables) throws Exception {
        String loopVarName = loopInfo.loopVarName();
        String loopContent = loopInfo.loopContent();
        int start = loopInfo.start();

        List<?> loopVar = (List<?>) variables.get(loopVarName);
        if (loopVar == null) {
            throw new Exception("Loop variable '" + loopVarName + "' is not defined.");
        }

        StringBuilder loopResult = new StringBuilder();
        for (int i = 0; i < loopVar.size(); i++) {
            Object item = loopVar.get(i);
            HashMap<String, Object> loopVariables = new HashMap<>(variables);

            if (item instanceof Map) {
                Map<String, Object> itemMap = (Map<String, Object>) item;
                for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                    loopVariables.put("this." + entry.getKey(), entry.getValue());
                }
            }
            loopVariables.put("@index", i);
            loopVariables.put("@last", (i == loopVar.size() - 1));

            String renderedContent = render(loopContent, loopVariables).stripLeading();
            loopResult.append(renderedContent);
        }

        template.insert(start, loopResult);
    }

    private void evaluateConditionals(StringBuilder template, HashMap<String, Object> variables) throws Exception {
        int start;
        while ((start = template.indexOf(IF_START)) != -1) {
            int ifEndIdx = template.indexOf(IF_END, start);
            if (ifEndIdx == -1) break;

            int conditionStartIdx = start + IF_START.length();
            int conditionEndIdx = template.indexOf("}}", conditionStartIdx);
            String condition = template.substring(conditionStartIdx, conditionEndIdx).trim();

            int contentStartIdx = conditionEndIdx + 2;
            int elseIdx = template.indexOf(ELSE_TOKEN, contentStartIdx);
            String trueContent = elseIdx == -1 ? template.substring(contentStartIdx, ifEndIdx) : template.substring(contentStartIdx, elseIdx);
            String falseContent = elseIdx == -1 ? "" : template.substring(elseIdx + ELSE_TOKEN.length(), ifEndIdx);

            template.delete(start, ifEndIdx + IF_END.length());

            boolean conditionResult = evaluateCondition(condition, variables);
            template.insert(start, conditionResult ? trueContent : falseContent);
        }
    }

    private void replaceVariables(StringBuilder template, HashMap<String, Object> variables) {
        int start;
        while ((start = template.indexOf(VARIABLE_PLACEHOLDER_PREFIX)) != -1) {
            int end = template.indexOf(VARIABLE_PLACEHOLDER_SUFFIX, start);
            if (end == -1) break;

            String placeholder = template.substring(start + VARIABLE_PLACEHOLDER_PREFIX.length(), end).trim();
            String value = evaluatePlaceholder(placeholder, variables);
            template.replace(start, end + VARIABLE_PLACEHOLDER_SUFFIX.length(), value);
        }
    }

    private String evaluatePlaceholder(String placeholder, HashMap<String, Object> variables) {
        // Check if it contains a function call
        int funcStart = placeholder.indexOf("(");
        int funcEnd = placeholder.indexOf(")");

        if (funcStart != -1 && funcEnd != -1) {
            String functionName = placeholder.substring(0, funcStart).trim();
            String variableName = placeholder.substring(funcStart + 1, funcEnd).trim();

            // Evaluate the variable
            String value = variables.get(variableName) != null ? variables.get(variableName).toString() : "";

            // Apply the function
            Function<String, String> function = FUNCTIONS_MAP.get(functionName);
            if (function != null) {
                return function.apply(value);
            } else {
                return value; // Return the original value if the function is not found
            }
        } else {
            // No function, simply return the variable value
            return variables.get(placeholder) != null ? variables.get(placeholder).toString() : "";
        }
    }

    private void replaceAllOccurrences(StringBuilder template, String placeholder, String value) {
        int start;
        while ((start = template.indexOf(placeholder)) != -1) {
            template.replace(start, start + placeholder.length(), value);
        }
    }

    private void checkUndefinedVariables(StringBuilder template) throws Exception {
        int start;
        while ((start = template.indexOf(VARIABLE_PLACEHOLDER_PREFIX)) != -1) {
            int end = template.indexOf(VARIABLE_PLACEHOLDER_SUFFIX, start);
            if (end != -1) {
                String undefinedVariable = template.substring(start, end + 1);
                throw new Exception("Undefined variable found: " + undefinedVariable);
            } else {
                break;
            }
        }
    }

    private boolean evaluateCondition(String condition, HashMap<String, Object> variables) throws Exception {
        if (condition.startsWith("!")) {
            String innerCondition = condition.substring(1).trim();
            return !evaluateSimpleCondition(innerCondition, variables);
        } else {
            return evaluateSimpleCondition(condition, variables);
        }
    }

    private boolean evaluateSimpleCondition(String condition, HashMap<String, Object> variables) throws Exception {
        if ("@last".equals(condition)) {
            Object lastFlag = variables.get("@last");
            if (lastFlag instanceof Boolean) {
                return (Boolean) lastFlag;
            } else {
                throw new Exception("Variable '@last' is not a boolean.");
            }
        } else {
            Object conditionValue = variables.get(condition);
            if (conditionValue == null) {
                throw new Exception("Condition variable '" + condition + "' is not defined.");
            }
            return conditionValue instanceof Boolean && (Boolean) conditionValue;
        }
    }

    private void processSpecialTags(StringBuilder template) {
        // Replace {{newline}} with '\n'
        replaceAllOccurrences(template, NEWLINE_TAG, "\n");

        // Replace {{tab}} with '\t'
        replaceAllOccurrences(template, TAB_TAG, "\t");
    }

    private record LoopInfo(String loopVarName, String loopContent, int start) {
    }
}
