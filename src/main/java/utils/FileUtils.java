package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.gson.GsonBuilder;
import genesis.connexion.Database;
import genesis.connexion.adapter.DatabaseTypeAdapter;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {

    public static void extractDir(String sourcedir, String target) {
        byte[] buffer = new byte[4096];
        File file = new File(sourcedir);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            unZip(target, buffer, zis);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void unZip(String target, byte[] buffer, ZipInputStream zis) throws IOException {
        ZipEntry ze = zis.getNextEntry();

        while (ze != null) {
            String fileName = ze.getName();
            File newFile = new File(target + File.separator + fileName);
            if (ze.isDirectory()) {
                newFile.mkdir();
            } else {

                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }

            }
            zis.closeEntry();
            ze = zis.getNextEntry();
        }
    }

    public static String getFileContent(String filePath) throws FileNotFoundException {
        StringBuilder content = new StringBuilder();
        File file = new File(filePath);

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                content.append(reader.nextLine()).append("\n");
            }
        }

        return content.toString();
    }

    public static void overwriteFileContent(String filePath, String content) throws IOException {
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(content);
        }

    }

    public static String minStart(String string) {
        return string.replaceFirst(String.valueOf(string.charAt(0)), String.valueOf(string.charAt(0)).toLowerCase());
    }

    public static String majStart(String string) {
        return string.replaceFirst(String.valueOf(string.charAt(0)), String.valueOf(string.charAt(0)).toUpperCase());
    }

    public static String toCamelCase(String string) {
        String[] words = string.split("_");
        StringBuilder camelCase = new StringBuilder(words[0].toLowerCase()); // Le premier mot reste en minuscule

        for (int i = 1; i < words.length; i++) {
            camelCase.append(majStart(words[i]));
        }

        return camelCase.toString();
    }

    public static String toKebabCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        input = input.replace("_", "");

        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!result.isEmpty()) {
                    result.append("-");
                }
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static void createFileStructure(String filePath) {
        filePath = filePath.replace("\\", "/");
        String[] folders = filePath.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (String folder : folders) {
            currentPath.append(folder).append("/");
            File file = new File(currentPath.toString());

            if (!file.exists()) {
                file.mkdir();
            }
        }
    }


    public static void createFile(String filePath, String fileName, String fileExtension, String fileContent) throws IOException {
        // creation de la structure du projet
        createFileStructure(filePath);

        // creation du fichier et son contenu
        createSimpleFile(filePath, fileName, fileExtension, fileContent);
    }

    public static void createSimpleFile(String filePath, String fileName, String fileExtension, String fileContent) throws IOException {
        // creation du fichier et son contenu
        File file = new File(filePath + "/" + fileName + "." + fileExtension);
        file.createNewFile();
        Files.write(file.toPath(), fileContent.getBytes());
    }


    public static void copyFile(String sourceFilePath, String destinationFilePath) throws IOException {
        Path destinationPath = Paths.get(destinationFilePath);

        try (InputStream inputStream = new FileInputStream(sourceFilePath)) {
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (NoSuchFileException e) {
            createFileStructure(destinationPath.toString());
        }
        catch (IOException e) {
            throw new IOException("Error when copying file: " + e.getMessage(), e);
        }
    }

    public static void copyDirectory(String sourceDir, String destDir) throws IOException {
        Path srcPath = Paths.get(sourceDir);
        Path destPath = Paths.get(destDir);

        if (!Files.exists(destPath)) {
            Files.createDirectories(destPath);
        }

        try (Stream<Path> paths = Files.walk(srcPath)) {
            paths.forEach(path -> {
                Path destination = destPath.resolve(srcPath.relativize(path));
                try {
                    if (Files.isDirectory(path)) {
                        Files.createDirectories(destination);
                    } else {
                        copyFile(path.toString(), destination.toString());
                    }
                } catch (IOException e) {
                    e.getMessage();
                }
            });
        }
    }

    public static void createDirectory(String filePath) {
        String filename = "";
        String currentChar = "";

        File file;
        for (int i = 0; i < filePath.toCharArray().length; ++i) {
            currentChar = String.valueOf(filePath.charAt(i));
            if (currentChar.equals("/")) {
                file = new File(filename);
                file.mkdir();
            }

            filename = filename + currentChar;
        }

        file = new File(filename);
        file.mkdir();
    }

    public static String formatReadable(String s) {
        String newString = s;
        newString = newString.replace("_", " ");
        char[] characts = newString.toCharArray();
        StringBuilder newWord = new StringBuilder();

        for (char charact : characts) {
            if (Character.isUpperCase(charact)) {
                newWord.append(" ").append(Character.toLowerCase(charact));
            } else {
                newWord.append(charact);
            }
        }

        newWord = new StringBuilder(majStart(newWord.toString()));
        return newWord.toString();
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        GsonBuilder builder = (new GsonBuilder()).registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());

        builder.registerTypeAdapter(Database.class, new DatabaseTypeAdapter());

        return builder.create().fromJson(json, clazz);
    }


    public static <T> T fromYamlFile(Class<T> clazz, String yamlFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(new File(yamlFilePath), clazz);
    }


    public static <T> T fromYaml(Class<T> clazz, String yamlContent) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(new StringReader(yamlContent), clazz);
    }


    public static String toJson(Object source) {
        GsonBuilder builder = (new GsonBuilder()).registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        return builder.create().toJson(source);
    }


}
