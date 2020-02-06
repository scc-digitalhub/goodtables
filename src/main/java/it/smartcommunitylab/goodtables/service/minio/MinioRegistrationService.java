package it.smartcommunitylab.goodtables.service.minio;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.common.NoSuchRegistrationException;
import it.smartcommunitylab.goodtables.minio.MinioException;
import it.smartcommunitylab.goodtables.minio.MinioNotificationsClient;
import it.smartcommunitylab.goodtables.model.BucketNotification;
import it.smartcommunitylab.goodtables.model.BucketRegistration;
import it.smartcommunitylab.goodtables.repository.BucketNotificationRepository;
import it.smartcommunitylab.goodtables.repository.BucketRegistrationRepository;

@Component
public class MinioRegistrationService {
    private final static Logger _log = LoggerFactory.getLogger(MinioRegistrationService.class);

    @Autowired
    MinioNotificationsClient minioClient;

    @Autowired
    BucketRegistrationRepository bucketRepository;

    @Autowired
    BucketNotificationRepository notificationRepository;

    /*
     * Buckets
     */

    public BucketRegistration addBucketRegistration(
            String spaceId, String userId,
            String bucket, String type) {
        // basic check for space
        // TODO replace with external service
        if (!bucket.startsWith(spaceId)) {
            throw new AccessDeniedException("space does not match");
        }

        _log.debug("add bucket registration for " + bucket + " type " + type);
        BucketRegistration reg = new BucketRegistration();
        reg.setBucket(bucket);
        reg.setType(type);

        reg.setUserId(userId);
        reg.setSpaceId(spaceId);

        // listener will trigger notification creation
        return bucketRepository.saveAndFlush(reg);
    }

    public BucketRegistration deleteBucketRegistration(
            long id) throws NoSuchRegistrationException {
        if (bucketRepository.existsById(id)) {
            _log.debug("delete bucket registration for " + String.valueOf(id));

            // fetch reg
            BucketRegistration reg = bucketRepository.getOne(id);
            String bucket = reg.getBucket();
            String type = reg.getType();

            // fetch notifications
            List<BucketNotification> notifications = notificationRepository.findByBucketAndType(bucket, type);

            for (BucketNotification bn : notifications) {
                try {
                    // unregister via minio notification
                    unregisterBucketNotification(bn);
                } catch (MinioException mex) {
                    _log.error("error unregistering notification for " + bucket + " type " + type);
                    // ignore since we need to delete local reg
                    // we won't care if notifications keep flowing to broker
                }

                // remove local reg to stop receiving messages
                // listener will disconnect client from brokers if needed
                notificationRepository.delete(bn);
            }

            // delete local registration now
            bucketRepository.delete(reg);

            return reg;
        } else {
            throw new NoSuchRegistrationException();
        }
    }

    public BucketRegistration getBucketRegistration(long id) throws NoSuchRegistrationException {
        if (bucketRepository.existsById(id)) {
            _log.debug("get bucket registration for " + String.valueOf(id));
            return bucketRepository.getOne(id);
        } else {
            throw new NoSuchRegistrationException();
        }
    }

    public List<BucketRegistration> getBucketRegistrations(long[] ids) {
        Iterable<Long> iter = () -> LongStream.of(ids).boxed().iterator();

        return bucketRepository.findAllById(iter);

    }

    public List<BucketRegistration> listBucketRegistration(
            String spaceId) {
        return bucketRepository.findBySpaceId(spaceId);
    }

    public long countBucketRegistration(
            String spaceId) {
        return bucketRepository.countBySpaceId(spaceId);
    }

    public List<BucketRegistration> listBucketRegistration(
            String spaceId,
            String bucket) {
        return bucketRepository.findBySpaceIdAndBucket(spaceId, bucket);
    }

    public long countBucketRegistration(
            String spaceId,
            String bucket) {
        return bucketRepository.countBySpaceIdAndBucket(spaceId, bucket);
    }

    /*
     * Internal use
     */

    public List<BucketRegistration> listBucketRegistration() {
        return bucketRepository.findAll();
    }

    public long countBucketRegistration() {
        return bucketRepository.count();
    }

    public List<BucketNotification> listBucketNotification() {
        return notificationRepository.findAll();
    }

    public long countBucketNotification() {
        return notificationRepository.count();
    }

    /*
     * Minio
     */

    public BucketNotification registerBucketNotification(String bucketName, String type)
            throws MinioException {
        BucketNotification bn = new BucketNotification();
        bn.setBucket(bucketName);
        bn.setType(type);

        bn = minioClient.registerNotification(bn);

        // save will trigger listener to register topic in clients
        return notificationRepository.saveAndFlush(bn);

    }

    public void unregisterBucketNotification(BucketNotification bn) throws MinioException {
        // unregister via minio notification
        minioClient.unregisterNotification(bn);
    }

    public void unregisterBucketNotification(long id) throws MinioException {
        // fetch
        if (notificationRepository.existsById(id)) {
            BucketNotification bn = notificationRepository.getOne(id);
            unregisterBucketNotification(bn);
        }
    }
}
