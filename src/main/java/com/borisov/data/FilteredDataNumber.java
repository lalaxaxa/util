package com.borisov.data;

import java.util.Collections;

public class FilteredDataNumber<T extends Number & Comparable<T>> extends FilteredData<T> {
    private StringParser<T> parser;
    public FilteredDataNumber(String name, StringParser<T> parser) {
        super(name);
        this.parser = parser;
    }

    @Override
    public void add(String s) {
        list.add(parser.parse(s));
    }

    @Override
    public String getFullStats() {
        int count = list.size();
        T min = Collections.min(list);
        T max = Collections.max(list);

        double sum = 0;
        for (T num : list) {
            sum += num.doubleValue();
        }

        double avg = (double) sum / count;


        boolean isFloatingPoint = min instanceof Double || min instanceof Float;
        String numberMask = isFloatingPoint ? "%g" : "%d";

        String format = "%s - Count: %d\tMin: " + numberMask + "\tMax: " + numberMask + "\tSum: %g\tAvg: %g";
        return String.format(format, name, count, min, max, sum, avg);
    }

}
