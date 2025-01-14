package mymovies.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Validator {
    private final Map<String, List<String>> errors = new HashMap<>();
    private final Map<String, Object> fields = new HashMap<>();

    public Validator setField(String field, Object value) {
        fields.put(field, value);
        return this;
    }

    public Validator required(String... fields) {
        for (String field : fields) {
            Object value = this.fields.get(field);
            if (value == null || value.toString().trim().isEmpty()) {
                addError(field, "The " + field + " field is required.");
            }
        }
        return this;
    }

    public Validator numeric(String... fields) {
        for (String field : fields) {
            Object value = this.fields.get(field);
            if (value != null && !value.toString().matches("^[0-9]*$")) {
                addError(field, "The " + field + " field must be numeric.");
            }
        }
        return this;
    }

    public Validator min(String field, int min) {
        Object value = fields.get(field);
        if (value != null) {
            if (value instanceof String) {
                if (((String) value).length() < min) {
                    addError(field, "The " + field + " field must be at least " + min + " characters.");
                }
            } else if (value instanceof Number) {
                if (((Number) value).doubleValue() < min) {
                    addError(field, "The " + field + " field must be at least " + min + ".");
                }
            }
        }
        return this;
    }

    public Validator max(String field, int max) {
        Object value = fields.get(field);
        if (value != null) {
            if (value instanceof String) {
                if (((String) value).length() > max) {
                    addError(field, "The " + field + " field must not exceed " + max + " characters.");
                }
            } else if (value instanceof Number) {
                if (((Number) value).doubleValue() > max) {
                    addError(field, "The " + field + " field must not exceed " + max + ".");
                }
            }
        }
        return this;
    }

    public boolean passes() {
        return errors.isEmpty();
    }

    private void addError(String field, String message) {
        errors.computeIfAbsent(field, k -> new ArrayList<>()).add(message);
    }

    public Map<String, List<String>> getErrors() {
        return errors;
    }
}