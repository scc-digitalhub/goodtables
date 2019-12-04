package it.smartcommunitylab.goodtables.service.minio;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.minio.ObjectStat;
import it.smartcommunitylab.goodtables.common.InvalidArgumentException;
import it.smartcommunitylab.goodtables.common.SystemException;
import it.smartcommunitylab.goodtables.minio.MinioBridge;
import it.smartcommunitylab.goodtables.minio.MinioException;
import it.smartcommunitylab.goodtables.model.BucketRegistration;
import it.smartcommunitylab.goodtables.repository.BucketNotificationRepository;
import it.smartcommunitylab.goodtables.repository.BucketRegistrationRepository;
import it.smartcommunitylab.goodtables.util.FileUtils;
import it.smartcommunitylab.goodtables.validator.Validator;
import it.smartcommunitylab.goodtables.validator.ValidatorFactory;

@Component
public class MinioValidationService {
    private final static Logger _log = LoggerFactory.getLogger(MinioValidationService.class);

    @Value("${validator.maxsize}")
    long maxSize;

    @Autowired
    MinioBridge minio;

    @Autowired
    ValidatorFactory factory;

    @Autowired
    BucketRegistrationRepository bucketRepository;

    @Autowired
    BucketNotificationRepository notificationRepository;

    public BucketRegistration findRegistration(String bucket, String key, String type) {
        // ignore key
        return bucketRepository.findByBucketAndType(bucket, type);
    }

    public String executeValidation(String bucket, String key, String type)
            throws InvalidArgumentException, MinioException, RuntimeException, SystemException {

        _log.debug("execute validation for  bucket " + bucket + " key " + key + " type " + type
                + " with thread " + Thread.currentThread().getName());

        try {
            String report = "";
            // check if bucket+key exists and collect stats
            _log.debug("fetch stats for bucket " + bucket + " key " + key);
            ObjectStat stat = minio.getObjectStat(bucket, key);
            long fileSize = stat.length();
            String contentType = stat.contentType();

            _log.debug("stats for bucket " + bucket + " key" + key +
                    ":  length " + String.valueOf(fileSize)
                    + " contentType " + contentType);

            String mimeType = FileUtils.getDefaultMimeType(type);

            // check if legit
            if (fileSize > maxSize) {
                throw new InvalidArgumentException("fileSize > maxSize");
            }

            if (!contentType.equals(mimeType)) {
                // just log
                _log.error("mimeType mismatch");
            }

            // fetch validator
            Validator validator = factory.getValidator(type);
            if (validator == null) {
                throw new RuntimeException("validator not found");
            }

            // fetch inputstream and make sure to close
            InputStream inputStream = minio.getObject(bucket, key);
            try {
                _log.debug("execute validator " + validator.getType());
                report = validator.validate(inputStream, mimeType);

                return report;
            } finally {
                inputStream.close();
            }

        } catch (RuntimeException rex) {
            throw rex;
        } catch (MinioException mex) {
            throw mex;
        } catch (Exception e) {
            throw new SystemException(e.getMessage());
        }

    }
}
