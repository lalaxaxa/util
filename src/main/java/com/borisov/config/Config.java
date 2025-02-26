package com.borisov.config;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.cli.*;

public class Config {
    private static final String FLAG_APPEND = "a";
    private static final String FLAG_SHORT_STAT = "s";
    private static final String FLAG_FULL_STAT = "f";
    private static final String FLAG_OUT_PATH = "o";
    private static final String FLAG_OUT_PREFIX = "p";

    private boolean appendMode;
    private boolean shortStatEnabled;
    private boolean fullStatEnabled;
    private String outPath;
    private String outPrefix;
    private List<String> fileNames;

    public Config() {
        appendMode = false;
        shortStatEnabled = false;
        fullStatEnabled = false;
        outPath = "";
        outPrefix = "";
        fileNames = new ArrayList<>();
    }

    public boolean isAppendMode() {
        return appendMode;
    }

    public boolean isShortStatEnabled() {
        return shortStatEnabled;
    }

    public boolean isFullStatEnabled() {
        return fullStatEnabled;
    }

    public String getOutPath() {
        return outPath;
    }

    public String getOutPrefix() {
        return outPrefix;
    }

    public List<String> getFileNames() {
        return fileNames;
    }


    @Override
    public String toString() {
        return "Config{" +
                "appendMode=" + appendMode +
                ", shortStatEnabled=" + shortStatEnabled +
                ", fullStatEnabled=" + fullStatEnabled +
                ", outPath='" + outPath + '\'' +
                ", outPrefix='" + outPrefix + '\'' +
                ", fileNames=" + fileNames +
                '}';
    }

    public void parse(String[] args) throws ParseException {
        // создаем объект для парсинга командной строки
        Options options = new Options();

        //определяем флаги и их параметры
        options.addOption(FLAG_APPEND, "append", false, "Enable append mode");
        options.addOption(FLAG_SHORT_STAT, "shortStat", false, "Enable short statistics");
        options.addOption(FLAG_FULL_STAT, "fullStat", false, "Enable full statistics");
        options.addOption(FLAG_OUT_PATH, "outPath", true, "Output path");
        options.addOption(FLAG_OUT_PREFIX, "prefix", true, "Output filename prefix");

        //создаем парсер и парсим аргументы
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        //обрабатываем флаги
        appendMode = cmd.hasOption(FLAG_APPEND);
        shortStatEnabled = cmd.hasOption(FLAG_SHORT_STAT);
        fullStatEnabled = cmd.hasOption(FLAG_FULL_STAT);

        //обрабатываем флаги с параметрами
        if (cmd.hasOption(FLAG_OUT_PATH)) {
            outPath = cmd.getOptionValue(FLAG_OUT_PATH);
        }else{
            outPath = System.getProperty("user.dir");
        }
        if (cmd.hasOption(FLAG_OUT_PREFIX)) {
            outPrefix = cmd.getOptionValue(FLAG_OUT_PREFIX);
        }

        //все-остальное это имена файлов
        String[] remainingArgs = cmd.getArgs();
        for (String file : remainingArgs) {
            fileNames.add(file);
        }
    }
}