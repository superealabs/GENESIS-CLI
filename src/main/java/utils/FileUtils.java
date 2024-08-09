package utils;

import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Scanner;
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

    public static void extractDirFromInsideJar(Class<?> c, String sourcedir, String target) throws Throwable {
        byte[] buffer = new byte[4096];

        try (ZipInputStream zis = new ZipInputStream(Objects.requireNonNull(c.getResourceAsStream(sourcedir)))) {
            unZip(target, buffer, zis);
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

    public static String getFileContentFromInsideJar(Class<?> c, String filePath) throws IOException {
        StringBuilder content = new StringBuilder();

        try (Scanner reader = new Scanner(Objects.requireNonNull(c.getResourceAsStream(filePath)))) {
            while (reader.hasNextLine()) {
                content.append(reader.nextLine()).append("\n");
            }
        }

        return content.toString();
    }

    public static void copyFileFromInsideJar(Class<?> c, String source, String target) throws Throwable {
        Path targetPath = Path.of(target);

        try (InputStream stream = c.getResourceAsStream(source)) {
            assert stream != null;
            Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

    }

    public static void overwriteFileContent(String filePath, String content) throws IOException {
        File file = new File(filePath);

        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(content);
        }

    }

    public static String minStart(String s) {
        return s.replaceFirst(String.valueOf(s.charAt(0)), String.valueOf(s.charAt(0)).toLowerCase());
    }

    public static String majStart(String s) {
        return s.replaceFirst(String.valueOf(s.charAt(0)), String.valueOf(s.charAt(0)).toUpperCase());
    }

    public static String toCamelCase(String s) {
        StringBuilder newString = new StringBuilder();
        boolean snakeTrail = false;

        for (int i = 0; i < s.toCharArray().length; ++i) {
            String c = String.valueOf(s.charAt(i));
            if (snakeTrail) {
                c = majStart(c);
                snakeTrail = false;
            }

            if (c.equals("_")) {
                c = "";
                snakeTrail = true;
            }

            newString.append(c);
        }

        return newString.toString();
    }


    public static void createFile(String filePath) throws IOException {
        filePath = filePath.replace("\\", "/");
        String filename = "";
        String currentChar = "";

        File file;
        for (int i = 0; i < filePath.toCharArray().length; ++i) {
            currentChar = String.valueOf(filePath.charAt(i));
            if (currentChar.equals("/") && !filename.equals(".") && !filename.isEmpty()) {
                file = new File(filename);
                file.mkdir();
            }

            filename = filename + currentChar;
        }

        file = new File(filename);
        file.createNewFile();
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

    public static String minAll(String s) {
        char[] characts = s.toCharArray();
        StringBuilder newWord = new StringBuilder();

        for (char charact : characts) {
            if (Character.isUpperCase(charact)) {
                newWord.append(Character.toLowerCase(charact));
            } else {
                newWord.append(charact);
            }
        }

        return newWord.toString();
    }

    public static <T> T fromJson(Class<T> clazz, String json) {
        GsonBuilder builder = (new GsonBuilder()).registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        return builder.create().fromJson(json, clazz);
    }

    public static String toJson(Object source) {
        GsonBuilder builder = (new GsonBuilder()).registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter());
        builder.registerTypeAdapter(LocalTime.class, new LocalTimeTypeAdapter());
        builder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter());
        return builder.create().toJson(source);
    }


    public static <T> T parse(Class<T> clazz, String value) {
        return fromJson(clazz, "\"" + value + "\"");
    }
}
