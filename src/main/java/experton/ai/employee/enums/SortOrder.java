package experton.ai.employee.enums;

public enum SortOrder {
    ASC, DESC;

    public static boolean isValid(String value) {
        if (value == null) return false;
        try {
            SortOrder.valueOf(value.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
