package com.borisov.data;

import java.util.Collections;
public class FilteredDataDouble extends FilteredData<Double> {
    public FilteredDataDouble(String name) {
        super(name);
    }

    @Override
    public void add(String s) {
        list.add(Double.parseDouble(s));
    }

    @Override
    public String getFullStats() {
        int count = list.size();
        double min = Collections.min(list);
        double max = Collections.max(list);

        double sum = 0;
        for(Double num : list){
            sum += num;
        }

        double avg = sum / count;

        return String.format("%s - Count: %d, Min: %.5f, Max: %.5f, Sum: %.5f, Avg: %.5f",
                name, count, min, max, sum, avg);
    }

}
