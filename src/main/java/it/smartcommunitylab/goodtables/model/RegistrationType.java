package it.smartcommunitylab.goodtables.model;

import java.util.LinkedList;
import java.util.List;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;

public enum RegistrationType {
    MINIO("minio");

    private final String value;

    private RegistrationType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    /*
     * Static helpers
     */
    public static RegistrationType fromString(String type) throws InvalidArgumentException {
        for (RegistrationType t : RegistrationType.values()) {
            if (type.equals(t.value)) {
                return t;
            }
        }

        throw new InvalidArgumentException("unknown type '" + type + "'");
    }

    public static List<RegistrationType> fromStringList(List<String> typeList) throws InvalidArgumentException {
        List<RegistrationType> list = new LinkedList<>();
        for (String type : typeList) {
            list.add(RegistrationType.fromString(type));
        }

        return list;
    }

    public static List<String> toStringList(List<RegistrationType> typeList) {
        List<String> list = new LinkedList<>();
        for (RegistrationType type : typeList) {
            list.add(type.toString());
        }

        return list;
    }
}
