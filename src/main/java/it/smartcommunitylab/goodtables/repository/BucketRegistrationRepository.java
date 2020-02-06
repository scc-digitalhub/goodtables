package it.smartcommunitylab.goodtables.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.smartcommunitylab.goodtables.model.BucketRegistration;

public interface BucketRegistrationRepository extends JpaRepository<BucketRegistration, Long> {

    Long countBySpaceId(String spaceId);

    List<BucketRegistration> findBySpaceId(String spaceId);

    Long countBySpaceIdAndUserId(String spaceId, String userId);

    List<BucketRegistration> findBySpaceIdAndUserId(String spaceId, String userId);

    Long countBySpaceIdAndBucket(String spaceId, String bucket);

    List<BucketRegistration> findBySpaceIdAndBucket(String spaceId, String bucket);

    BucketRegistration findByBucketAndType(String bucket, String type);
}
