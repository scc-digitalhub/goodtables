package it.smartcommunitylab.goodtables.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.smartcommunitylab.goodtables.model.ValidationResult;

public interface ValidationResultRepository extends JpaRepository<ValidationResult, Long> {

    // scoped
    Long countByScopeIdAndKindAndName(String scopeId, String kind, String name);

    List<ValidationResult> findByScopeIdAndKindAndName(String scopeId, String kind, String name);

    Long countByScopeIdAndKindAndNameAndKey(String scopeId, String kind, String name, String key);

    List<ValidationResult> findByScopeIdAndKindAndNameAndKey(String scopeId, String kind, String name, String key);

//    // unscoped - DISABLED
//    Long countByKindAndName(String kind, String name);
//
//    List<ValidationResult> findByKindAndName(String kind, String name);
//
//    Long countByKindAndNameAndKey(String kind, String name, String key);
//
//    List<ValidationResult> findByKindAndNameAndKey(String kind, String name, String key);

}
