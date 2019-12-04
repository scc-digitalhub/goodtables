package it.smartcommunitylab.goodtables.model;

import java.util.LinkedList;
import java.util.List;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;

public enum ValidationType {
    CSV("csv"),
    TSV("tsv"),
    JSON("json"),
    XLS("xls"),
    XLSX("xlsx");

    private final String value;

    private ValidationType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    /*
     * Static helpers
     */
    public static ValidationType fromString(String type) throws InvalidArgumentException {
        for (ValidationType t : ValidationType.values()) {
            if (type.equals(t.value)) {
                return t;
            }
        }

        throw new InvalidArgumentException("unknown type '" + type + "'");
    }

    public static List<ValidationType> fromStringList(List<String> typeList) throws InvalidArgumentException {
        List<ValidationType> list = new LinkedList<>();
        for (String type : typeList) {
            list.add(ValidationType.fromString(type));
        }

        return list;
    }

    public static List<String> toStringList(List<ValidationType> typeList) {
        List<String> list = new LinkedList<>();
        for (ValidationType type : typeList) {
            list.add(type.toString());
        }

        return list;
    }
}
