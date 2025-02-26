package com.borisov;

import com.borisov.config.Config;
import com.borisov.data.*;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class App {

    private static final String LONG_FILENAME = "integers";
    private static final String DOUBLE_FILENAME = "floats";
    private static final String STRING_FILENAME = "strings";

    public static void main(String[] args) {
        //args = new String[]{"-a", "-s", "-f", "-o", "src/out", "-p", "result_", "src/in/in1.txt", "src/in/in2.txt"};
        if (args.length == 0) {
            System.err.println("Использование: [-a] [-s] [-f] [-o] <output-path> [-p] <output-prefix> <input-files...>");
            return;
        }

        Config config = parseConfig(args);
        if (config == null) return;

        List<String> lines = readAllFiles(config.getFileNames());
        if (lines.isEmpty()) {
            System.err.println("Ошибка: не удалось получить исходные данные!");
            return;
        }

        Map<FilteredData.Type, FilteredData<?>> map = createFilteredDataMap();
        filterLines(map, lines);
        processFilteredData(map, config);
        System.out.println("\nOK");
    }

    //парсинг конфига
    private static Config parseConfig(String[] args) {
        Config config = new Config();
        try {
            config.parse(args);
            System.out.println(config);
            return config;
        } catch (ParseException e) {
            System.err.println("Ошибка при разборе аргументов:" + e.getMessage());
            return null;
        }
    }

    //чтение всех файлов в один список строк
    private static List<String> readAllFiles(List<Path> fileNames) {
        List<String> lines = new ArrayList<>();
        for (Path filePath : fileNames) {
            try (BufferedReader br = Files.newBufferedReader(filePath)) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            } catch (Exception e) {
                System.err.println("Ошибка при считывании файла " + filePath + ": " + e.getMessage());
            }
        }
        //System.out.println("lines = " + lines);
        return lines;
    }

    //запись данных в файл
    private static void writeFile(FilteredData<?> data, Config config, Path filePath) {
        Path filename = filePath.getFileName();
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                !config.isAppendMode() ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.APPEND,
                StandardOpenOption.CREATE)) {
            for (Object obj : data.getList()) {
                writer.write(obj.toString());
                writer.newLine();
            }
            System.out.println("Файл " + filename + " успешно записан!");
        } catch (Exception e) {
            System.err.println("Ошибка при записи файла " + filename + ": " + e.getMessage());
        }
    }

    // cоздание директорий, если их нет
    private static boolean createDirs(Path filePath) {
        Path parentDir = filePath.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
                System.out.println("Созданы необходимые директории: " + parentDir);
                return true;
            } catch (IOException | Error e) {
                System.err.println("Ошибка при создании директории " + parentDir + ": " + e.getMessage());
                return false;
            }

        }
        return true;
    }


    //создание Map с различными типами данных
    private static Map<FilteredData.Type, FilteredData<?>> createFilteredDataMap() {
        Map<FilteredData.Type, FilteredData<?>> map = new HashMap<>();
        map.put(FilteredData.Type.LONG, new FilteredDataLong(LONG_FILENAME));
        map.put(FilteredData.Type.DOUBLE, new FilteredDataDouble(DOUBLE_FILENAME));
        map.put(FilteredData.Type.STRING, new FilteredDataString(STRING_FILENAME));
        return map;
    }

    //фильтрация строк по их типу
    private static void filterLines(Map<FilteredData.Type, FilteredData<?>> map, List<String> lines) {
        for (String line : lines) {
            FilteredData.Type type = FilteredData.getType(line);
            map.get(type).add(line);
        }
    }

    //вывод статистики
    private static void showStats(FilteredData<?> data, Config config) {
        if (config.isShortStatEnabled() && !config.isFullStatEnabled()) {
            System.out.println("\n" + data.getShortStats());
        }
        if (config.isFullStatEnabled()) {
            System.out.println("\n" + data.getFullStats());
        }
        //System.out.println(data.getList());
    }

    //обработка и запись данных в файлы
    private static void processFilteredData(Map<FilteredData.Type, FilteredData<?>> map, Config config) {
        for (FilteredData<?> data : map.values()) {
            if (!data.getList().isEmpty()) {
                //вывести статистику
                showStats(data, config);
                //записать в файл
                Path filePath = config.getOutPath().resolve(config.getOutPrefix() + data.getName() + ".txt");
                if (createDirs(filePath)){
                    writeFile(data, config, filePath);
                }
            }
        }
    }
}
