package it.smartcommunitylab.goodtables.validator;

import java.io.File;
import java.io.InputStream;

import it.smartcommunitylab.goodtables.common.SystemException;

public interface Validator {

    public String getType();

    public String validate(File file) throws RuntimeException, SystemException;

    public String validate(InputStream inputStream, String mimeType) throws RuntimeException, SystemException;

}
