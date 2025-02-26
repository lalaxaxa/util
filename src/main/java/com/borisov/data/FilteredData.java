package com.borisov.data;

import java.util.ArrayList;
import java.util.List;

public abstract class FilteredData<T> implements Stats {
    protected String name;
    protected List<T> list;
    public FilteredData(String name) {
        this.name = name;
        this.list = new ArrayList<>();
    }
    public static Type getType(String s) {
        if (s == null) {
            throw new NullPointerException("Входная строка не может быть null");
        }

        s = s.trim();

        try {
            Long.parseLong(s);
            return Type.LONG;
        } catch (NumberFormatException e) {
        }

        try {
            Double.parseDouble(s);
            return Type.DOUBLE;
        } catch (NumberFormatException e) {
        }

        return Type.STRING;
    }

    public abstract void add(String s);
    public abstract String getFullStats();
    public String getShortStats() {
        return String.format("%s - Count: %d",
                name, list.size());
    }

    public String getName() {
        return name;
    }

    public List<T> getList() {
        return list;
    }


    public enum Type {
        LONG, DOUBLE, STRING;
    }
}