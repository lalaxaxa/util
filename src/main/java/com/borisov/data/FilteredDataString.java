package com.borisov.data;
public class FilteredDataString extends FilteredData<String> {
    public FilteredDataString(String name) {
        super(name);
    }

    @Override
    public void add(String s) {
        list.add(s);
    }

    @Override
    public String getFullStats() {
        int count = list.size();

        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;

        for (String line : list) {
            int length = line.length();
            if (length < minLength) {
                minLength = length;
            }
            if (length > maxLength) {
                maxLength = length;
            }
        }

        return String.format("%s - Count: %d, Min Length: %d, Max Length: %d",
                name, count, minLength, maxLength);
    }

}
