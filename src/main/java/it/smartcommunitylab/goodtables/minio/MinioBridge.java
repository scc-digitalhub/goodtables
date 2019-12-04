package it.smartcommunitylab.goodtables.minio;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidArgumentException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.NoResponseException;

@Component
public class MinioBridge {
    private final static Logger _log = LoggerFactory.getLogger(MinioBridge.class);

    @Value("${minio.endpoint}")
    private String ENDPOINT;

    @Value("${minio.port}")
    private int PORT;

    @Value("${minio.secure}")
    private boolean SECURE;

    @Value("${minio.region}")
    private String REGION;

    @Value("${minio.accessKey}")
    private String ACCESS_KEY;

    @Value("${minio.secretKey}")
    private String SECRET_KEY;

    /*
     * Buckets
     */

    public boolean hasBucket(String name) throws MinioException {
        try {
            MinioClient minio = getClient(name);
            return minio.bucketExists(name);
        } catch (InvalidEndpointException | InvalidPortException | InvalidKeyException | InvalidBucketNameException
                | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidResponseException
                | IOException | XmlPullParserException e) {
            e.printStackTrace();
            throw new MinioException(e);
        }
    }

    /*
     * Objects
     */

    public InputStream getObject(String bucket, String key) throws MinioException {
        try {
            MinioClient minio = getClient(bucket);
            return minio.getObject(bucket, key);
        } catch (InvalidEndpointException | InvalidPortException | InvalidKeyException | InvalidBucketNameException
                | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidResponseException
                | IOException | XmlPullParserException | InvalidArgumentException e) {
            e.printStackTrace();
            throw new MinioException(e);
        }
    }

    public ObjectStat getObjectStat(String bucket, String key) throws MinioException {
        try {
            MinioClient minio = getClient(bucket);
            return minio.statObject(bucket, key);
        } catch (InvalidKeyException | InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
                | NoResponseException | ErrorResponseException | InternalException | InvalidResponseException
                | InvalidArgumentException | IOException | XmlPullParserException | InvalidEndpointException
                | InvalidPortException e) {
            e.printStackTrace();
            throw new MinioException(e);
        }

    }
    /*
     * Client
     */

    private MinioClient getClient(String bucket) throws InvalidEndpointException, InvalidPortException {
        // TODO implement dynamic via STS
        // use global credentials
        return getClient();

    }

    private MinioClient getClient() throws InvalidEndpointException, InvalidPortException {
        // use global credentials
        _log.debug("create client for " + ENDPOINT + ":" + String.valueOf(PORT) + " with accessKey " + ACCESS_KEY);
        if (StringUtils.isEmpty(REGION)) {
            return new MinioClient(ENDPOINT, PORT, ACCESS_KEY, SECRET_KEY, REGION, SECURE);
        } else {
            return new MinioClient(ENDPOINT, PORT, ACCESS_KEY, SECRET_KEY, SECURE);

        }

    }

}
