package it.smartcommunitylab.goodtables.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.RandomStringUtils;

import it.smartcommunitylab.goodtables.common.SystemException;

public class FileUtils {

    /*
     * Temp files
     */

    public static File createTempFile(String extension) {
        String basePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
        String tempPath = basePath + "/temp";

        File folder = new File(tempPath);
        if (!folder.exists()) {
            folder.mkdir();
        }

        String fileName = RandomStringUtils.randomAlphanumeric(12) + "." + extension;

        return new File(folder + "/" + fileName);

    }

    public static void deleteTempFile(File file) {
        try {
            // verify path
            String basePath = FileSystems.getDefault().getPath("").toAbsolutePath().toString();
            String tempPath = basePath + "/temp";
            if (file.exists()) {
                String path = file.getAbsoluteFile().getParent();

                if (path.equals(tempPath)) {
                    // delete file
                    file.delete();

                }
            }
        } catch (Exception ioex) {
            // ignore
        }
    }

    public static File createTempFile(InputStream inputStream, String extension) throws SystemException {
        File temp = createTempFile(extension);
        try {
            Files.copy(inputStream,
                    temp.toPath(),
                    StandardCopyOption.REPLACE_EXISTING);

            return temp;
        } catch (IOException iox) {
            deleteTempFile(temp);
            throw new SystemException("io-error");
        }

    }

    /*
     * Helpers
     */

    public static String getDefaultMimeType(String extension) {
        String mimeType = "application/octect-stream";

        switch (extension) {
        case "csv":
            mimeType = "text/csv";
            break;
        case "json":
            mimeType = "application/json";
            break;

        }

        return mimeType;
    }
}
