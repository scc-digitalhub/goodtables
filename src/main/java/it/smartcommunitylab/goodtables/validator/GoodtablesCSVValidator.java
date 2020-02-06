package it.smartcommunitylab.goodtables.validator;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import it.smartcommunitylab.goodtables.common.SystemException;
import it.smartcommunitylab.goodtables.model.ValidationStatus;
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
    public Pair<Integer, String> validate(File file) throws RuntimeException, SystemException {
        _log.debug("validate csv file " + file.getAbsolutePath());
        GoodtablesCommand goodtables = new GoodtablesCommand();
        try {
            Pair<Integer, String> result = goodtables.validate(file);
            // we expect a JSON from command, parse and check
            String report = result.getSecond();
            JSONObject json = new JSONObject(report);
            if (json.isEmpty()) {
                throw new JSONException("empty-response");
            }
            return Pair.of(result.getFirst(), json.toString());
        } catch (JSONException | IOException | InterruptedException e) {
            e.printStackTrace();
            throw new SystemException(e.getMessage());
        }

    }

    @Override
    public Pair<Integer, String> validate(InputStream inputStream, String mimeType)
            throws RuntimeException, SystemException {

        // fetch as file, can't stream
        String extension = getType();
        File file = null;
        int status = ValidationStatus.PROCESSING.value();
        String report = "";
        try {
            file = FileUtils.createTempFile(inputStream, extension);
            Pair<Integer, String> result = validate(file);
            status = result.getFirst();
            report = result.getSecond();
        } catch (SystemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            status = ValidationStatus.ERROR.value();
        } finally {
            if (file != null) {
                FileUtils.deleteTempFile(file);
            }
        }

        return Pair.of(status, report);
    }

}
