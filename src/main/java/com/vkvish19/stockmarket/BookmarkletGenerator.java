package com.vkvish19.stockmarket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BookmarkletGenerator
{

    private static final String JS_PATH = "src/main/resources/stockmarket/javascript/TV_Watchlist.js";
    private static final String JSON_PATH = "src/main/resources/stockmarket/data/vk_stocks.json";

    // Define the fixed order for the Main Groups
    private static final List<String> MAIN_GROUP_ORDER = Arrays.asList("VK40", "VK40 Next", "VK200");

    public static void main(String[] args)
    {
        try
        {
            String bookmarklet = generate();
            System.out.println("--- GENERATED BOOKMARKLET ---");
            System.out.println(bookmarklet);
            System.out.println("-----------------------------");
        }
        catch (IOException e)
        {
            System.err.println("Error generating bookmarklet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Comparator for Level 2 (Sectors) and Level 3 (Stocks): "ALL" first, then Alpha
    private static final Comparator<String> allFirstComparator = (a, b) -> {
        String nameA = a.contains(":") ? a.split(":")[1] : a;
        String nameB = b.contains(":") ? b.split(":")[1] : b;

        if (nameA.equalsIgnoreCase("ALL"))
        {
            return -1;
        }
        if (nameB.equalsIgnoreCase("ALL"))
        {
            return 1;
        }
        return nameA.compareToIgnoreCase(nameB);
    };

    /**
     * Reads the JS template and JSON data, merges them, and minifies the output.
     */
    public static String generate() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        String rawJson = readFile(JSON_PATH);
        Map<String, Map<String, List<String>>> data = mapper.readValue(rawJson,
                new TypeReference<Map<String, Map<String, List<String>>>>()
                {
                });

        Map<String, Map<String, List<String>>> sortedData = sortData(data);
        String sortedJsonString = mapper.writeValueAsString(sortedData);

        String jsTemplate = readFile(JS_PATH);
        String mergedJs = jsTemplate.replace("{{DATA_PLACEHOLDER}}", sortedJsonString);

        return minify(mergedJs);
    }

    private static Map<String, Map<String, List<String>>> sortData(Map<String, Map<String, List<String>>> data)
    {
        // Use LinkedHashMap to preserve the insertion order of MAIN_GROUP_ORDER
        Map<String, Map<String, List<String>>> result = new LinkedHashMap<>();

        // 1. Process Main Groups in the specific order defined in MAIN_GROUP_ORDER
        for (String groupName : MAIN_GROUP_ORDER)
        {
            if (data.containsKey(groupName))
            {
                result.put(groupName, sortSectorsAndStocks(data.get(groupName)));
            }
        }

        // 2. Catch-all for any groups NOT in your priority list (sorted alphabetically)
        data.keySet().stream()
                .filter(key -> !MAIN_GROUP_ORDER.contains(key))
                .sorted()
                .forEach(key -> result.put(key, sortSectorsAndStocks(data.get(key))));

        return result;
    }

    private static Map<String, List<String>> sortSectorsAndStocks(Map<String, List<String>> sectors)
    {
        // Use TreeMap for Sectors to keep them alphabetical (with "ALL" logic)
        Map<String, List<String>> sortedSectors = new TreeMap<>(allFirstComparator);

        sectors.forEach((sectorName, stocks) -> {
            List<String> sortedStocks = new ArrayList<>(stocks);
            sortedStocks.sort(allFirstComparator);
            sortedSectors.put(sectorName, sortedStocks);
        });

        return sortedSectors;
    }

    private static String readFile(String path) throws IOException
    {
        return new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
    }

    private static String minify(String js)
    {
        return Arrays.stream(js.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.startsWith("//") && !line.startsWith("/*"))
                .filter(line -> !line.endsWith("*/"))
                .collect(Collectors.joining(" "))
                .replaceAll("\\s+", " ")
                .replaceAll("\\s?([\\{\\}\\(\\)=,:;])\\s?", "$1");
    }
}