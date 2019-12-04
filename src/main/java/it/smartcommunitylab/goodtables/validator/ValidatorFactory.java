package it.smartcommunitylab.goodtables.validator;

import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;
import it.smartcommunitylab.goodtables.model.ValidationType;

@Component
public class ValidatorFactory {

    /*
     * Builders
     */
    public Validator getValidator(String type) throws InvalidArgumentException {

        // build at each invocation
        // could use a cache or singletons
        Validator val = null;

        // get type via enum, will trigger exception if unknown
        ValidationType t = ValidationType.fromString(type);
        switch (t) {
        case CSV:
            val = new GoodtablesCSVValidator();
            break;
        case JSON:
            break;
        case TSV:
            break;
        case XLS:
            break;
        case XLSX:
            break;
        default:
            break;
        }

        if (val == null) {
            throw new InvalidArgumentException("no validator for " + type);
        }

        return val;
    }

}
