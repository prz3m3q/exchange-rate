package pl.com.bottega.exchangerate.domain.commands;

import java.time.LocalDate;
import java.util.*;

public interface Validatable {

    void validate(ValidationErrors errors);

    class ValidationErrors {

        private Map<String, Set<String>> errors = new HashMap<>();

        public void add(String fieldName, String errorMessage) {
            Set<String> fieldErrors = errors.getOrDefault(fieldName, new HashSet<>());
            fieldErrors.add(errorMessage);
            errors.putIfAbsent(fieldName, fieldErrors);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public Map<String, Set<String>> getErrors() {
            return new HashMap<>(errors);
        }

    }

    default boolean isEmpty(String s) {
        return s == null || s.trim().length() == 0;
    }

    default boolean isEmpty(LocalDate s) {
        return s == null;
    }

    default boolean isEmpty(Double s) {
        return s == null;
    }

}
