package it.smartcommunitylab.goodtables.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;
import it.smartcommunitylab.goodtables.common.NoSuchRegistrationException;
import it.smartcommunitylab.goodtables.common.NoSuchValidationResultException;
import it.smartcommunitylab.goodtables.model.BucketRegistration;
import it.smartcommunitylab.goodtables.model.RegistrationDTO;
import it.smartcommunitylab.goodtables.model.ValidationResult;
import it.smartcommunitylab.goodtables.model.ValidationResultDTO;
import it.smartcommunitylab.goodtables.repository.ValidationResultRepository;
import it.smartcommunitylab.goodtables.service.minio.MinioValidationService;

@Component
public class ValidationService {
    private final static Logger _log = LoggerFactory.getLogger(ValidationService.class);

    @Autowired
    ValidationResultRepository repository;

    @Autowired
    MinioValidationService minioService;

    /*
     * Results
     */

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'WRITE')")
    public ValidationResultDTO addResult(
            String scopeId, String userId,
            String kind, String name, String key, String type,
            String report) {
        _log.debug("add result for " + kind + " name " + name + " key " + key + " type " + type);
        ValidationResult result = _addResult(kind, name, key, type, report, scopeId, userId);
        return ValidationResultDTO.fromResult(result);
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public ValidationResultDTO getResult(
            String scopeId, String userId,
            long id) throws NoSuchValidationResultException {
        if (repository.existsById(id)) {
            _log.debug("get result for " + String.valueOf(id));
            ValidationResult res = repository.getOne(id);

            if (res.getScopeId().equals(scopeId)) {
                return ValidationResultDTO.fromResult(res);
            } else {
                throw new AccessDeniedException("scope does not match");
            }

        } else {
            throw new NoSuchValidationResultException();
        }
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'DELETE')")
    public ValidationResultDTO deleteResult(
            String scopeId, String userId,
            long id) throws NoSuchValidationResultException {
        if (repository.existsById(id)) {
            _log.debug("delete result for " + String.valueOf(id));
            ValidationResult res = repository.getOne(id);

            if (res.getScopeId().equals(scopeId)) {
                // delete, nothing else to do
                repository.delete(res);

                return ValidationResultDTO.fromResult(res);
            } else {
                throw new AccessDeniedException("scope does not match");
            }

        } else {
            throw new NoSuchValidationResultException();
        }
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public long countResult(
            String scopeId, String userId,
            String kind, String name) {
        return repository.countByScopeIdAndKindAndName(scopeId, kind, name);
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String scopeId, String userId,
            String kind, String name) {
        return repository.findByScopeIdAndKindAndName(scopeId, kind, name).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public long countResult(
            String scopeId, String userId,
            String kind, String name, String key) {
        return repository.countByScopeIdAndKindAndNameAndKey(scopeId, kind, name, key);
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String scopeId, String userId,
            String kind, String name, String key) {
        return repository.findByScopeIdAndKindAndNameAndKey(scopeId, kind, name, key).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    /*
     * Executors
     */
    @Async("threadPoolTaskExecutor")
    public void executeValidation(String kind, String name, String key, String type) {
        _log.debug("execute validation for " + kind
                + " name " + name + " key " + key + " type " + type
                + " with thread " + Thread.currentThread().getName());

        try {
            // init
            String report = "";
            String userId = "";
            String scopeId = "";
            RegistrationDTO reg = null;

            // execute with validator service
            switch (kind) {
            case "minio":
                BucketRegistration br = minioService.findRegistration(name, key, type);
                if (br == null) {
                    throw new NoSuchRegistrationException("no registration for " + name + " type " + type);
                }
                reg = RegistrationDTO.fromRegistration(br);
                report = minioService.executeValidation(name, key, type);
                break;
            default:
                throw new InvalidArgumentException("unknown validator kind");
            }

            if (reg != null) {
                userId = reg.getUserId();
                scopeId = reg.getType();
            }

            // build result and store
            ValidationResult result = _addResult(kind, name, key, type, report, scopeId, userId);
            _log.debug("validation result in " + String.valueOf(result.getId()));

        } catch (Exception e) {
            _log.error("error validation for " + kind
                    + " name " + name + " key " + key + " type " + type
                    + ": " + e.getMessage());
            e.printStackTrace();
        }

    }

    /*
     * Protected
     */
    private ValidationResult _addResult(
            String kind, String name, String key, String type,
            String report,
            String scopeId, String userId) {
        _log.debug("add result for " + kind + " name " + name + " key " + key + " type " + type);
        ValidationResult res = new ValidationResult();
        res.setKind(kind);
        res.setName(name);
        res.setKey(key);
        res.setType(type);
        res.setReport(report);

        res.setScopeId(scopeId);
        res.setUserId(userId);

        return repository.saveAndFlush(res);
    }
}
