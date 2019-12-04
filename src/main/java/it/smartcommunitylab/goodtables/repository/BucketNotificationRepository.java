package it.smartcommunitylab.goodtables.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.smartcommunitylab.goodtables.model.BucketNotification;

public interface BucketNotificationRepository extends JpaRepository<BucketNotification, Long> {

    BucketNotification findByTopic(String topic);

    Long countByBucket(String bucket);

    List<BucketNotification> findByBucket(String bucket);

    Long countByBucketAndType(String bucket, String type);

    List<BucketNotification> findByBucketAndType(String bucket, String type);
}
