package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import genesis.connexion.Database;
import genesis.connexion.adapter.DatabaseDeserializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.Stream;

public class FileUtils {

    public static String getFileContent(String resourcePath) throws IOException {
        StringBuilder content = new StringBuilder();

        // Utilisation du class loader pour charger la ressource
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)
        ) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + resourcePath);
            }
            try (Scanner reader = new Scanner(inputStream)) {
                while (reader.hasNextLine()) {
                    content.append(reader.nextLine()).append("\n");
                }
            }
        }

        return content.toString();
    }

    public static String getFileContentSQL(String filePath) throws FileNotFoundException {
        File file = new File(filePath);

        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        StringBuilder content = new StringBuilder();
        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (!line.isEmpty() && !line.startsWith("--")) {
                    content.append(line).append(" ");
                }
            }
        }

        String finalContent = content.toString().trim();
        if (finalContent.isEmpty()) {
            throw new FileNotFoundException("The file is empty or contains only comments: " + filePath);
        }

        return finalContent.replaceAll("\\s+", " ").replaceAll(";\\s*", ";");
    }

    public static String removeLastS(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        char lastChar = input.charAt(input.length() - 1);
        if (lastChar == 's' || lastChar == 'S') {
            return input.substring(0, input.length() - 1);
        }

        return input;
    }


    public static String minStart(String string) {
        return string.transform(s -> s.replaceFirst(String.valueOf(s.charAt(0)), String.valueOf(s.charAt(0)).toLowerCase()));
    }

    public static String toCamelCase(String string) {
        return string.transform(s -> {
            String[] words = s.split("_");
            StringBuilder camelCase = new StringBuilder(words[0].toLowerCase());

            for (int i = 1; i < words.length; i++) {
                camelCase.append(majStart(words[i]));
            }

            return camelCase.toString();
        });
    }

    public static String toKebabCase(String input) {
        return input.transform(s -> {
            if (s.isEmpty()) {
                return s;
            }

            StringBuilder result = new StringBuilder();
            s = s.replace("_", "");

            for (char c : s.toCharArray()) {
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
        });
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
        File file = new File(filePath + "/" + fileName + "." + fileExtension);
        if (file.exists()) {
            file.delete();
        }
        Files.write(file.toPath(), fileContent.getBytes());
    }

    public static void copyFile(String sourceFilePath, String destinationFilePath, String fileName) throws IOException {
        Path destinationPath = Paths.get(destinationFilePath, fileName);

        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(sourceFilePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: " + sourceFilePath);
            }
            createFileStructure(destinationPath.getParent().toString());
            Files.copy(inputStream, destinationPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Error when copying file: " + e.getMessage(), e);
        }
    }


    public static void copyDirectory(String sourceDir, String destDir) throws IOException {
        URI resourceUri = getResourceUri(sourceDir);

        Path destPath = Paths.get(destDir);
        if ("jar".equals(resourceUri.getScheme())) {
            // Resource is in a JAR
            try (FileSystem fileSystem = FileSystems.newFileSystem(resourceUri, Collections.emptyMap())) {
                Path jarPath = fileSystem.getPath(sourceDir);
                copyDirectoryFromJar(jarPath, destPath);
            }
        } else {
            // Resource is in the file system
            Path srcPath = Paths.get(resourceUri);
            copyDirectoryFromFileSystem(srcPath, destPath);
        }
    }


    private static URI getResourceUri(String sourceDir) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource(sourceDir);
        if (resourceUrl == null) {
            throw new IOException("Resource directory not found: " + sourceDir);
        }
        try {
            return resourceUrl.toURI();
        } catch (URISyntaxException e) {
            throw new IOException("Error converting URL to URI for resource: " + sourceDir, e);
        }
    }

    private static void copyDirectoryFromJar(Path srcPath, Path destPath) throws IOException {
        try (Stream<Path> paths = Files.walk(srcPath)) {
            paths.forEach(path -> {
                try {
                    Path relativePath = srcPath.relativize(path);
                    Path destination = destPath.resolve(relativePath.toString()); // Ensure compatibility

                    if (Files.isDirectory(path)) {
                        if (!Files.exists(destination)) {
                            Files.createDirectories(destination);
                        }
                    } else {
                        try (InputStream inputStream = Files.newInputStream(path)) {
                            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error copying file from JAR: " + path + " -> " + e.getMessage());
                }
            });
        }
    }



    private static void copyDirectoryFromFileSystem(Path srcPath, Path destPath) throws IOException {
        try (Stream<Path> paths = Files.walk(srcPath)) {
            paths.forEach(path -> {
                Path relativePath = srcPath.relativize(path);
                Path destination = destPath.resolve(relativePath);
                try {
                    if (Files.isDirectory(path)) {
                        if (!Files.exists(destination)) {
                            Files.createDirectories(destination);
                        }
                    } else {
                        Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    System.err.println("Error copying file: " + path + " -> " + e.getMessage());
                }
            });
        }
    }


    public static void createDirectory(String filePath) {
        String filename = "";
        String currentChar;

        File file;
        for (int i = 0; i < filePath.length(); ++i) {
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

    public static <T> T fromJson(Class<T> clazz, String resourcePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        SimpleModule module = new SimpleModule();
        module.addDeserializer(Database.class, new DatabaseDeserializer());
        objectMapper.registerModule(module);

        // Load the file from the classpath
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("File not found: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, clazz);
        }
    }

    public static <T> T fromYaml(Class<T> clazz, String resourcePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

        // Load the file from the classpath
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new FileNotFoundException("File not found: " + resourcePath);
            }
            return objectMapper.readValue(inputStream, clazz);
        }
    }

    public static <T> T fromYamlFile(Class<T> clazz, String yamlFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(new File(yamlFilePath), clazz);
    }

    public static String majStart(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }


}
