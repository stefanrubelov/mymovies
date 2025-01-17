package mymovies.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Env {
    private static final Map<String, String> envVariables = new HashMap<>();

    public static void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    envVariables.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading .env file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        load();
        try {
            if (!envVariables.containsKey(key)) {
                throw new IOException("Key not found in environment variables: " + key);
            }
            return envVariables.get(key);
        } catch (IOException e) {
            System.err.println("Error retrieving environment variable: " + e.getMessage());
            return null;
        }
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }
}
