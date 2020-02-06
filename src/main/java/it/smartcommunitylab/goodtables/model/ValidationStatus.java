package it.smartcommunitylab.goodtables.model;

public enum ValidationStatus {
    VALID(0),
    INVALID(1),
    UNKNOWN(-1),
    QUEUED(2),
    PROCESSING(3),
    ERROR(4);

    private int value;

    private ValidationStatus(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }

    public String toString() {
        return Integer.toString(this.value);
    }
}
