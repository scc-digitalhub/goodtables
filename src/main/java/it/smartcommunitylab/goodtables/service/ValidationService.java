package it.smartcommunitylab.goodtables.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
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
import it.smartcommunitylab.goodtables.model.ValidationStatus;
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

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'WRITE')")
    public ValidationResultDTO addResult(
            String spaceId, String userId,
            String kind, String name, String key, String type,
            int status, String report) {
        _log.debug("add result for " + kind + " name " + name + " key " + key + " type " + type);
        ValidationResult result = _addResult(kind, name, key, type, status, report, spaceId, userId);
        return ValidationResultDTO.fromResult(result);
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public ValidationResultDTO getResult(
            String spaceId, String userId,
            long id) throws NoSuchValidationResultException {
        if (repository.existsById(id)) {
            _log.debug("get result for " + String.valueOf(id));
            ValidationResult res = repository.getOne(id);

            if (res.getSpaceId().equals(spaceId)) {
                return ValidationResultDTO.fromResult(res);
            } else {
                throw new AccessDeniedException("space does not match");
            }

        } else {
            throw new NoSuchValidationResultException();
        }
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> getResults(
            String spaceId, String userId,
            long[] ids) {
        _log.debug("get result for " + String.valueOf(ids));
        Iterable<Long> iter = () -> LongStream.of(ids).boxed().iterator();
        return repository.findAllById(iter).stream()
                .filter(r -> spaceId.equals(r.getSpaceId()))
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'DELETE')")
    public ValidationResultDTO deleteResult(
            String spaceId, String userId,
            long id) throws NoSuchValidationResultException {
        if (repository.existsById(id)) {
            _log.debug("delete result for " + String.valueOf(id));
            ValidationResult res = repository.getOne(id);

            if (res.getSpaceId().equals(spaceId)) {
                // delete, nothing else to do
                repository.delete(res);

                return ValidationResultDTO.fromResult(res);
            } else {
                throw new AccessDeniedException("space does not match");
            }

        } else {
            throw new NoSuchValidationResultException();
        }
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public long countResult(
            String spaceId, String userId) {
        return repository.countBySpaceId(spaceId);
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId) {
        return repository.findBySpaceId(spaceId).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId,
            Pageable page) {
        return repository.findBySpaceId(spaceId, page).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public long countResult(
            String spaceId, String userId,
            String kind) {
        return repository.countBySpaceIdAndKind(spaceId, kind);
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId,
            String kind) {
        return repository.findBySpaceIdAndKind(spaceId, kind).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId,
            String kind,
            Pageable page) {
        return repository.findBySpaceIdAndKind(spaceId, kind, page).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public long countResult(
            String spaceId, String userId,
            String kind, String name) {
        return repository.countBySpaceIdAndKindAndName(spaceId, kind, name);
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId,
            String kind, String name) {
        return repository.findBySpaceIdAndKindAndName(spaceId, kind, name).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId,
            String kind, String name,
            Pageable page) {
        return repository.findBySpaceIdAndKindAndName(spaceId, kind, name, page).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public long countResult(
            String spaceId, String userId,
            String kind, String name, String key) {
        return repository.countBySpaceIdAndKindAndNameAndKey(spaceId, kind, name, key);
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId,
            String kind, String name, String key) {
        return repository.findBySpaceIdAndKindAndNameAndKey(spaceId, kind, name, key).stream()
                .map(r -> ValidationResultDTO.fromResult(r)).collect(Collectors.toList());
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<ValidationResultDTO> listResult(
            String spaceId, String userId,
            String kind, String name, String key,
            Pageable page) {
        return repository.findBySpaceIdAndKindAndNameAndKey(spaceId, kind, name, key, page).stream()
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
            int status = ValidationStatus.UNKNOWN.value();
            String report = "";
            String userId = "";
            String spaceId = "";
            RegistrationDTO reg = null;

            // execute with validator service
            switch (kind) {
            case "minio":
                BucketRegistration br = minioService.findRegistration(name, key, type);
                if (br == null) {
                    throw new NoSuchRegistrationException("no registration for " + name + " type " + type);
                }
                reg = RegistrationDTO.fromRegistration(br);
                Pair<Integer, String> res = minioService.executeValidation(name, key, type);
                status = res.getFirst();
                report = res.getSecond();
                break;
            default:
                throw new InvalidArgumentException("unknown validator kind");
            }

            if (reg != null) {
                userId = reg.getUserId();
                spaceId = reg.getType();
            }

            // build result and store
            ValidationResult result = _addResult(kind, name, key, type, status, report, spaceId, userId);
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
            int status, String report,
            String spaceId, String userId) {
        _log.debug("add result for " + kind + " name " + name + " key " + key + " type " + type + " status "
                + String.valueOf(status));
        ValidationResult res = new ValidationResult();
        res.setKind(kind);
        res.setName(name);
        res.setKey(key);
        res.setType(type);
        res.setStatus(status);
        res.setReport(report);

        res.setSpaceId(spaceId);
        res.setUserId(userId);

        return repository.saveAndFlush(res);
    }
}
