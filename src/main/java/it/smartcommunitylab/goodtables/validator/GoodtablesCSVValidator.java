package it.smartcommunitylab.goodtables.validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.smartcommunitylab.goodtables.common.SystemException;
import it.smartcommunitylab.goodtables.model.ValidationType;
import it.smartcommunitylab.goodtables.util.FileUtils;
import it.smartcommunitylab.goodtables.validator.goodtables.GoodtablesCommand;

public class GoodtablesCSVValidator extends BaseValidator {
    private final static Logger _log = LoggerFactory.getLogger(GoodtablesCSVValidator.class);

    @Override
    public String getType() {
        return ValidationType.CSV.toString();
    }

    @Override
    public String validate(File file) throws RuntimeException, SystemException {
        _log.debug("validate csv file " + file.getAbsolutePath());
        GoodtablesCommand goodtables = new GoodtablesCommand();
        try {
            String result = goodtables.validate(file);
            // we expect a JSON from command, parse and check
            JSONObject json = new JSONObject(result);
            if (json.isEmpty()) {
                throw new JSONException("empty-response");
            }
            return json.toString();
        } catch (JSONException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new SystemException(e.getMessage());
        }

    }

    @Override
    public String validate(InputStream inputStream, String mimeType) throws RuntimeException, SystemException {

        // fetch as file, can't stream
        String extension = getType();
        File file = null;
        String report = "";
        try {
            file = FileUtils.createTempFile(inputStream, extension);
            report = validate(file);
        } catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (file != null) {
                FileUtils.deleteTempFile(file);
            }
        }

        return report;
    }

}
