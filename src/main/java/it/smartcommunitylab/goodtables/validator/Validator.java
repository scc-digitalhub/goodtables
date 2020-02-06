package it.smartcommunitylab.goodtables.validator;

import java.io.File;
import java.io.InputStream;

import org.springframework.data.util.Pair;

import it.smartcommunitylab.goodtables.common.SystemException;

public interface Validator {

    public String getType();

    public Pair<Integer, String> validate(File file) throws RuntimeException, SystemException;

    public Pair<Integer, String> validate(InputStream inputStream, String mimeType)
            throws RuntimeException, SystemException;

}
