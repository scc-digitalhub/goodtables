package it.smartcommunitylab.goodtables.validator.goodtables;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringJoiner;

import org.springframework.data.util.Pair;

import it.smartcommunitylab.goodtables.model.ValidationStatus;

public class GoodtablesCommand {

    private final String goodtables = "goodtables";

    private String output;
    private String preset;

    public GoodtablesCommand() {
        super();
        this.preset = "nested";
        this.output = "";
    }

    public GoodtablesCommand(String preset) {
        super();
        this.preset = preset;
        this.output = "";
    }

    public Pair<Integer, String> validate(File file) throws IOException, InterruptedException, RuntimeException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.directory(file.getParentFile());
        builder.command(goodtables, "validate", "--json", "--infer-schema", "--preset", preset, file.getName());
        Process process = builder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringJoiner sj = new StringJoiner(System.getProperty("line.separator"));
        reader.lines().iterator().forEachRemaining(sj::add);
        int ret = process.waitFor();
        // disable ret check, goodtables return > 0 for invalid files
//        if (ret != 0) {
//            throw new RuntimeException("gootables exited with code " + String.valueOf(ret));
//        }
        // use ret as status code
        int status = ValidationStatus.UNKNOWN.value();
        if (ret == 0) {
            status = ValidationStatus.VALID.value();
        } else {
            status = ValidationStatus.INVALID.value();
        }
        output = sj.toString();
        return Pair.of(status, output);
    }

}
