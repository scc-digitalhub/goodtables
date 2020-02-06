package it.smartcommunitylab.goodtables.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import it.smartcommunitylab.goodtables.model.ValidationResult;

public interface ValidationResultRepository extends JpaRepository<ValidationResult, Long> {

    Long countBySpaceId(String spaceId);

    List<ValidationResult> findBySpaceId(String spaceId);

    List<ValidationResult> findBySpaceId(String spaceId, Pageable page);

    Long countBySpaceIdAndKind(String spaceId, String kind);

    List<ValidationResult> findBySpaceIdAndKind(String spaceId, String kind);

    List<ValidationResult> findBySpaceIdAndKind(String spaceId, String kind, Pageable page);

    Long countBySpaceIdAndKindAndName(String spaceId, String kind, String name);

    List<ValidationResult> findBySpaceIdAndKindAndName(String spaceId, String kind, String name);

    List<ValidationResult> findBySpaceIdAndKindAndName(String spaceId, String kind, String name, Pageable page);

    Long countBySpaceIdAndKindAndNameAndKey(String spaceId, String kind, String name, String key);

    List<ValidationResult> findBySpaceIdAndKindAndNameAndKey(String spaceId, String kind, String name, String key);

    List<ValidationResult> findBySpaceIdAndKindAndNameAndKey(String spaceId, String kind, String name, String key,
            Pageable page);

//    // unscoped - DISABLED
//    Long countByKindAndName(String kind, String name);
//
//    List<ValidationResult> findByKindAndName(String kind, String name);
//
//    Long countByKindAndNameAndKey(String kind, String name, String key);
//
//    List<ValidationResult> findByKindAndNameAndKey(String kind, String name, String key);

}
