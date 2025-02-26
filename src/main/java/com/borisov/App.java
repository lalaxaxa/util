package com.borisov;

import com.borisov.config.Config;
import com.borisov.data.*;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        //получить конфиг
        Config config = new Config();
        getConfig(args, config);
        System.out.println(config.toString());

        //считать исходные данные
        List<String> lines = new ArrayList<>();
        for (String filePath : config.getFileNames()) {
            try {
                readFiles(lines, filePath);
            } catch (IOException e) {
                System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            }

        }
        System.out.println(lines);
        if (lines.size() == 0){
            System.out.println("Ошибка, не удалось получить исходные данные!");
            return;
        }


        //создать map для разных типов FilteredData с ключом FilteredData.Type
        Map<FilteredData.Type, FilteredData> map = new HashMap<>();
        //заполнить map
        fillMap(map);
        //отфильтровать lines в map
        filterLines(map, lines);

        for (FilteredData data: map.values()) {
            if (data.getList().size() != 0){
                //вывести статистику
                showStats(data, config);

                //записать в файл
                try {
                    writeFile(data, config);
                } catch (IOException e) {
                    System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
                }
            }
        }

    }

    private static void getConfig(String[] args, Config config){
        try {
            config.parse(args);
        } catch (ParseException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            return;
        }
    }

    private static void readFiles(List<String> lines, String filePath) throws IOException{
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw e;
        }
    }
    private static void writeFile(FilteredData data, Config config) throws IOException{
        Path filePath = Paths.get(config.getOutPath() + "/" + config.getOutPrefix() + data.getName() + ".txt");

        try {
            // используем BufferedWriter для записи по строкам
            BufferedWriter writer = Files.newBufferedWriter(filePath,
                    !config.isAppendMode() ? StandardOpenOption.CREATE : StandardOpenOption.CREATE,
                    !config.isAppendMode() ? StandardOpenOption.TRUNCATE_EXISTING : StandardOpenOption.APPEND
            );
            for (Object obj : data.getList()) {
                writer.write(obj.toString());
                writer.newLine();
            }
            writer.close();
            System.out.println("Файл " + data.getName() + ".txt успешно записан!");
        } catch (IOException e) {
            throw e;
        }
    }


    private static void fillMap(Map<FilteredData.Type, FilteredData> map){
        map.put(FilteredData.Type.LONG, new FilteredDataLong(LONG_FILENAME));
        map.put(FilteredData.Type.DOUBLE, new FilteredDataDouble(DOUBLE_FILENAME));
        map.put(FilteredData.Type.STRING, new FilteredDataString(STRING_FILENAME));
    }

    private static void filterLines(Map<FilteredData.Type, FilteredData> map, List<String> lines){
        for (String line : lines) {
            FilteredData filteredData = map.get(FilteredData.getType(line));
            filteredData.add(line);
        }
    }

    private static void showStats(FilteredData data, Config config){
        System.out.println("\n" + data.getList());
        if (config.isShortStatEnabled() && !config.isFullStatEnabled()){
            System.out.println(data.getShortStats());
        }
        if (config.isFullStatEnabled()){
            System.out.println(data.getFullStats());
        }
    }


}
