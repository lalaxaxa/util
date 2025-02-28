package com.borisov.config;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.*;

public class Config {
    private static final String FLAG_APPEND = "a";
    private static final String FLAG_SHORT_STAT = "s";
    private static final String FLAG_FULL_STAT = "f";
    private static final String FLAG_OUT_PATH = "o";
    private static final String FLAG_OUT_PREFIX = "p";

    private static boolean isPath(String str){
        try{
            Paths.get(str);
            return true;
        }catch (InvalidPathException e){
            System.err.println("Путь \"" + str + "\" некорретный");
            return false;
        }
    }
    private boolean appendMode;
    private boolean shortStatEnabled;
    private boolean fullStatEnabled;
    private Path outPath;
    private Path outPrefix;

    private Set<Path> fileNames;

    public Config() {
        appendMode = false;
        shortStatEnabled = false;
        fullStatEnabled = false;
        outPath = Paths.get("");
        outPrefix = Paths.get("");
        fileNames = new HashSet<>();
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

    public Path getOutPath() {
        return outPath;
    }
    private void setOutPath(String outPath) {
        if(isPath(outPath)){
            this.outPath = Paths.get(outPath);
        }
    }

    public Path getOutPrefix() {
        return outPrefix;
    }

    private void setOutPrefix(String outPrefix) {
        if(isPath(outPrefix)){
            Path path = Paths.get(outPrefix);
            if (path.getNameCount() > 1){
                System.err.println("\"" + outPrefix + "\" некорретное значение для outPrefix");
            }else {
                this.outPrefix = path;
            }
        }
    }

    public Set<Path> getFileNames() {
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
            setOutPath(cmd.getOptionValue(FLAG_OUT_PATH));
        } else{
            setOutPath(System.getProperty("user.dir"));
        }
        if (cmd.hasOption(FLAG_OUT_PREFIX)) {
            setOutPrefix(cmd.getOptionValue(FLAG_OUT_PREFIX));
        }

        //все-остальное это имена файлов
        String[] remainingArgs = cmd.getArgs();
        for (String file : remainingArgs) {
            if(isPath(file)){
                fileNames.add(Paths.get(file).toAbsolutePath());
            }

        }
    }
}