package it.smartcommunitylab.goodtables.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.smartcommunitylab.goodtables.model.BucketRegistration;

public interface BucketRegistrationRepository extends JpaRepository<BucketRegistration, Long> {

    Long countByScopeId(String scopeId);

    List<BucketRegistration> findByScopeId(String scopeId);

    Long countByScopeIdAndUserId(String scopeId, String userId);

    List<BucketRegistration> findByScopeIdAndUserId(String scopeId, String userId);

    Long countByScopeIdAndBucket(String scopeId, String bucket);

    List<BucketRegistration> findByScopeIdAndBucket(String scopeId, String bucket);

    BucketRegistration findByBucketAndType(String bucket, String type);
}
