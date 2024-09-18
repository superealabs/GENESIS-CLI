package genesis.engine;

import utils.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TemplateEngine {

    private static final String LOOP_START = "{{#each ";
    private static final String LOOP_INDEX = "@index";
    private static final String IS_LOOP_LAST_INDEX = "@last";
    private static final String LOOP_ITEM = "this";
    private static final String LOOP_END = "{{/each}}";
    private static final String IF_START = "{{#if ";
    private static final String ELSE_IF_TOKEN = "{{elseIf ";
    private static final String ELSE_TOKEN = "{{else}}";
    private static final String IF_END = "{{/if}}";
    private static final String VARIABLE_PLACEHOLDER_PREFIX = "${";
    private static final String VARIABLE_PLACEHOLDER_SUFFIX = "}";
    private static final String NEWLINE_TAG = "{{newline}}";
    private static final String TAB_TAG = "{{tab}}";
    private static final String BLOCK_END = "}}";
    private static final String FUNCTION_OPEN_PARENTHESIS = "(";
    private static final String FUNCTION_CLOSED_PARENTHESIS = ")";


    private static final Map<String, Function<String, String>> FUNCTIONS_MAP = new HashMap<>();

    static {
        FUNCTIONS_MAP.put("upperCase", str -> str == null ? "" : str.toUpperCase());
        FUNCTIONS_MAP.put("lowerCase", str -> str == null ? "" : str.toLowerCase());
        FUNCTIONS_MAP.put("majStart", FileUtils::majStart);
        FUNCTIONS_MAP.put("toCamelCase", FileUtils::toCamelCase);
    }

    public String simpleRender(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("The template must not be empty.");
        }

        StringBuilder result = new StringBuilder(template);

        int start = 0;
        while ((start = result.indexOf(VARIABLE_PLACEHOLDER_PREFIX, start)) != -1) {
            int end = result.indexOf(VARIABLE_PLACEHOLDER_SUFFIX, start);
            if (end == -1) break;

            String placeholder = result.substring(start + VARIABLE_PLACEHOLDER_PREFIX.length(), end).trim();
            String value = evaluatePlaceholderSimple(placeholder, variables);
            result.replace(start, end + VARIABLE_PLACEHOLDER_SUFFIX.length(), value);

            start += value.length();
        }

        return result.toString();
    }


    private String evaluatePlaceholderSimple(String placeholder, Map<String, Object> variables) {
        int funcStart = placeholder.indexOf(FUNCTION_OPEN_PARENTHESIS);
        int funcEnd = placeholder.indexOf(FUNCTION_CLOSED_PARENTHESIS);

        if (funcStart != -1 && funcEnd != -1 && funcStart < funcEnd) {
            String functionName = placeholder.substring(0, funcStart).trim();
            String variableName = placeholder.substring(funcStart + 1, funcEnd).trim();

            Object valueObj = variables.get(variableName);
            String value = valueObj != null ? valueObj.toString() : VARIABLE_PLACEHOLDER_PREFIX + variableName + VARIABLE_PLACEHOLDER_SUFFIX;

            Function<String, String> function = FUNCTIONS_MAP.get(functionName);
            if (function != null) {
                return function.apply(value);
            } else {
                return VARIABLE_PLACEHOLDER_PREFIX + placeholder + VARIABLE_PLACEHOLDER_SUFFIX;
            }
        } else {
            Object valueObj = variables.get(placeholder);
            return valueObj != null ? valueObj.toString() : VARIABLE_PLACEHOLDER_PREFIX + placeholder + VARIABLE_PLACEHOLDER_SUFFIX;
        }
    }


    public String render(String template, Map<String, Object> variables) throws Exception {
        if (template == null || template.isEmpty()) {
            throw new IllegalArgumentException("The template must not be empty.");
        }

        if (variables == null || variables.isEmpty()) {
            return template;
        }

        StringBuilder result = new StringBuilder(template);

        evaluateLoops(result, variables);

        evaluateConditionals(result, variables);

        replaceVariables(result, variables);

        processSpecialTags(result);

        checkUndefinedVariables(result);

        return result.toString();
    }

    private void evaluateLoops(StringBuilder template, Map<String, Object> variables) throws Exception {
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
        int loopVarEndIdx = template.indexOf(BLOCK_END, loopVarStartIdx);
        String loopVarName = template.substring(loopVarStartIdx, loopVarEndIdx).trim();

        int contentStartIdx = loopVarEndIdx + BLOCK_END.length();
        String loopContent = template.substring(contentStartIdx, loopEndIdx);

        template.delete(start, loopEndIdx + LOOP_END.length());

        return new LoopInfo(loopVarName, loopContent, start);
    }

    @SuppressWarnings("unchecked")
    private void processLoopContent(StringBuilder template, LoopInfo loopInfo, Map<String, Object> variables) throws Exception {
        String loopVarName = loopInfo.loopVarName();
        String loopContent = loopInfo.loopContent();
        int start = loopInfo.start();

        List<?> loopVar = (List<?>) variables.get(loopVarName);
        if (loopVar == null) {
            return;
        }

        StringBuilder loopResult = new StringBuilder();
        for (int i = 0; i < loopVar.size(); i++) {
            Object item = loopVar.get(i);
            Map<String, Object> loopVariables = new HashMap<>(variables);

            if (item instanceof Map) {
                Map<String, Object> itemMap = (Map<String, Object>) item;
                for (Map.Entry<String, Object> entry : itemMap.entrySet()) {
                    loopVariables.put(LOOP_ITEM + "." + entry.getKey(), entry.getValue());
                }
            } else {
                loopVariables.put(LOOP_ITEM, item);
            }

            loopVariables.put(LOOP_INDEX, i);
            loopVariables.put(IS_LOOP_LAST_INDEX, (i == loopVar.size() - 1));

            String renderedContent = render(loopContent, loopVariables).stripLeading();
            loopResult.append(renderedContent);
        }

        template.insert(start, loopResult);
    }


    private void evaluateConditionals(StringBuilder template, Map<String, Object> variables) throws Exception {
        int start;
        while ((start = template.indexOf(IF_START)) != -1) {
            int ifEndIdx = findBlockEnd(template, start);
            if (ifEndIdx == -1) break;

            String condition = extractCondition(template, start);
            int contentStartIdx = findContentStartIdx(template, start);

            StringBuilder resultContent = new StringBuilder();
            if (evaluateCondition(condition, variables)) {
                resultContent.append(extractIfContent(template, contentStartIdx, ifEndIdx));
            } else {
                processElseIfBlocks(template, variables, contentStartIdx, ifEndIdx, resultContent);
            }

            replaceBlockWithResult(template, start, ifEndIdx, resultContent);
        }
    }

    private int findBlockEnd(StringBuilder template, int start) {
        return template.indexOf(IF_END, start);
    }

    private String extractCondition(StringBuilder template, int start) {
        int conditionStartIdx = start + IF_START.length();
        int conditionEndIdx = template.indexOf(BLOCK_END, conditionStartIdx);
        return template.substring(conditionStartIdx, conditionEndIdx).trim();
    }

    private int findContentStartIdx(StringBuilder template, int start) {
        int conditionEndIdx = template.indexOf(BLOCK_END, start + IF_START.length());
        return conditionEndIdx + BLOCK_END.length();
    }

    private String extractIfContent(StringBuilder template, int contentStartIdx, int ifEndIdx) {
        int elseIfIdx = template.indexOf(ELSE_IF_TOKEN, contentStartIdx);
        int elseIdx = template.indexOf(ELSE_TOKEN, contentStartIdx);
        int ifBlockEnd = determineBlockEnd(elseIfIdx, elseIdx, ifEndIdx);
        return template.substring(contentStartIdx, ifBlockEnd);
    }

    private void processElseIfBlocks(StringBuilder template, Map<String, Object> variables,
                                     int contentStartIdx, int ifEndIdx, StringBuilder resultContent) throws Exception {
        int elseIfIdx = template.indexOf(ELSE_IF_TOKEN, contentStartIdx);
        int elseIdx = template.indexOf(ELSE_TOKEN, contentStartIdx);
        boolean conditionMatched = false;

        while (elseIfIdx != -1 && elseIfIdx < ifEndIdx && !conditionMatched) {
            String elseIfCondition = extractElseIfCondition(template, elseIfIdx);
            contentStartIdx = findContentStartIdx(template, elseIfIdx);

            int blockEnd = determineNextBlockEnd(template, contentStartIdx, ifEndIdx);
            String falseContent = template.substring(contentStartIdx, blockEnd);

            if (evaluateCondition(elseIfCondition, variables)) {
                resultContent.append(falseContent);
                conditionMatched = true;
            }

            elseIfIdx = template.indexOf(ELSE_IF_TOKEN, contentStartIdx);
        }

        if (!conditionMatched) {
            processElseBlock(template, elseIdx, ifEndIdx, resultContent);
        }
    }

    private String extractElseIfCondition(StringBuilder template, int elseIfIdx) {
        int elseIfConditionEndIdx = template.indexOf(BLOCK_END, elseIfIdx);
        return template.substring(elseIfIdx + ELSE_IF_TOKEN.length(), elseIfConditionEndIdx).trim();
    }

    private int determineNextBlockEnd(StringBuilder template, int contentStartIdx, int ifEndIdx) {
        int nextElseIfIdx = template.indexOf(ELSE_IF_TOKEN, contentStartIdx);
        int nextElseIdx = template.indexOf(ELSE_TOKEN, contentStartIdx);
        return determineBlockEnd(nextElseIfIdx, nextElseIdx, ifEndIdx);
    }

    private void processElseBlock(StringBuilder template, int elseIdx, int ifEndIdx, StringBuilder resultContent) {
        if (elseIdx != -1 && elseIdx < ifEndIdx) {
            String elseContent = template.substring(elseIdx + ELSE_TOKEN.length(), ifEndIdx);
            resultContent.append(elseContent);
        }
    }

    private int determineBlockEnd(int elseIfIdx, int elseIdx, int ifEndIdx) {
        if (elseIfIdx != -1 && elseIfIdx < ifEndIdx) {
            return elseIfIdx;
        }
        if (elseIdx != -1 && elseIdx < ifEndIdx) {
            return elseIdx;
        }
        return ifEndIdx;
    }

    private void replaceBlockWithResult(StringBuilder template, int start, int ifEndIdx, StringBuilder resultContent) {
        template.delete(start, ifEndIdx + IF_END.length());
        template.insert(start, resultContent);
    }



    private void replaceVariables(StringBuilder template, Map<String, Object> variables) {
        int start;
        while ((start = template.indexOf(VARIABLE_PLACEHOLDER_PREFIX)) != -1) {
            int end = template.indexOf(VARIABLE_PLACEHOLDER_SUFFIX, start);
            if (end == -1) break;

            String placeholder = template.substring(start + VARIABLE_PLACEHOLDER_PREFIX.length(), end).trim();
            String value = evaluatePlaceholder(placeholder, variables);
            template.replace(start, end + VARIABLE_PLACEHOLDER_SUFFIX.length(), value);
        }
    }

    private String evaluatePlaceholder(String placeholder, Map<String, Object> variables) {
        int funcStart = placeholder.indexOf("(");
        int funcEnd = placeholder.indexOf(")");

        if (funcStart != -1 && funcEnd != -1 && funcStart < funcEnd) {
            String functionName = placeholder.substring(0, funcStart).trim();
            String variableName = placeholder.substring(funcStart + 1, funcEnd).trim();

            Object valueObj = variables.get(variableName);
            String value = valueObj != null ? valueObj.toString() : "";


            Function<String, String> function = FUNCTIONS_MAP.get(functionName);
            if (function != null) {
                return function.apply(value);
            } else {
                return value;
            }
        } else {

            Object valueObj = variables.get(placeholder);
            return valueObj != null ? valueObj.toString() : "";
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

    private boolean evaluateCondition(String condition, Map<String, Object> variables) throws Exception {

        condition = condition.trim();


        if (condition.startsWith("!")) {
            String innerCondition = condition.substring(1).trim();
            return !evaluateCondition(innerCondition, variables);
        }


        return evaluateCompositeCondition(condition, variables);
    }

    private boolean evaluateCompositeCondition(String condition, Map<String, Object> variables) throws Exception {

        String[] orConditions = condition.split("\\s+or\\s+");
        for (String orCondition : orConditions) {

            String[] andConditions = orCondition.split("\\s+and\\s+");
            boolean andResult = true;
            for (String andCondition : andConditions) {

                andCondition = andCondition.trim();
                boolean result = evaluateSimpleCondition(andCondition, variables);
                andResult = andResult && result;
            }

            if (andResult) {
                return true;
            }
        }

        return false;
    }


    private boolean evaluateSimpleCondition(String condition, Map<String, Object> variables) throws Exception {

        if ("true".equalsIgnoreCase(condition)) {
            return true;
        }
        if ("false".equalsIgnoreCase(condition)) {
            return false;
        }

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
                return false;
            }
            return conditionValue instanceof Boolean && (Boolean) conditionValue;
        }
    }

    private void processSpecialTags(StringBuilder template) {

        replaceAllOccurrences(template, NEWLINE_TAG, "\n");


        replaceAllOccurrences(template, TAB_TAG, "\t");
    }

    private record LoopInfo(String loopVarName, String loopContent, int start) {
    }
}
