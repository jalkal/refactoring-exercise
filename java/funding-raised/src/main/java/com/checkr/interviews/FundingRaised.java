package com.checkr.interviews;

import java.util.*;
import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FundingRaised {
    public static List<Map<String, String>> where(Map<String, String> options) throws IOException {
        return filterByOptions(options);
    }

    public static Map<String, String> findBy(Map<String, String> options) throws IOException, NoSuchEntryException {
        List<Map<String, String>> csvData = filterByOptions(options);
        if(csvData.size()>0) return csvData.get(0);
        throw new NoSuchEntryException();
    }

    private static List<Map<String, String>> filterByOptions(Map<String, String> options) throws IOException {
        List<Map<String, String>> csvData = loadDataFromFile();
        Predicate<Map<String, String>> filters = options.entrySet().stream().map(FundingRaised::aPredicate).reduce(Predicate::and).orElse(x->false);
        return csvData.stream().filter(filters).collect(Collectors.toList());
    }
    private static Predicate<Map<String, String>> aPredicate(Map.Entry<String, String> columnValue){
        return row -> row.get(columnValue.getKey()).equals(columnValue.getValue());
    }

    public static void main(String[] args) {
        try {
            Map<String, String> options = new HashMap<String, String> ();
            options.put("company_name", "Facebook");
            options.put("round", "a");
            System.out.print(FundingRaised.where(options).size());
        } catch(IOException e) {
            System.out.print(e.getMessage());
            System.out.print("error");
        }
    }

    private static List<Map<String, String>> loadDataFromFile() throws IOException {
        CSVReader reader = new CSVReader(new FileReader("startup_funding.csv"));
        List<String[]> csvData = reader.readAll();
        reader.close();
        String[] columns = csvData.get(0);
        return csvData.stream().skip(1).map(row -> mapRowToMap(row, columns)).collect(Collectors.toList());
    }
    private static Map<String, String> mapRowToMap(String[] row, String[] columns) {
        return IntStream.range(0, columns.length).boxed().collect(Collectors.toMap(index -> columns[index], i -> row[i]));
    }
}

class NoSuchEntryException extends Exception {}
