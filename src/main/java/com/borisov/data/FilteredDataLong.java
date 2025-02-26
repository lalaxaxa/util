package com.borisov.data;

import java.util.Collections;
public class FilteredDataLong extends FilteredData<Long> {
    public FilteredDataLong(String name) {
        super(name);
    }

    @Override
    public void add(String s) {
        list.add(Long.parseLong(s));
    }

    @Override
    public String getFullStats() {
        int count = list.size();
        long min = Collections.min(list);
        long max = Collections.max(list);

        long sum = 0;
        for(Long num : list){
            sum += num;
        }

        double avg = (double) sum / count;

        return String.format("%s - Count: %d, Min: %d, Max: %d, Sum: %d, Avg: %.5f",
                name, count, min, max, sum, avg);
    }

}
