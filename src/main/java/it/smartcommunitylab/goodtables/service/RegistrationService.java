package it.smartcommunitylab.goodtables.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;
import it.smartcommunitylab.goodtables.common.NoSuchRegistrationException;
import it.smartcommunitylab.goodtables.common.SystemException;
import it.smartcommunitylab.goodtables.model.BucketRegistration;
import it.smartcommunitylab.goodtables.model.RegistrationDTO;
import it.smartcommunitylab.goodtables.model.RegistrationType;
import it.smartcommunitylab.goodtables.service.minio.MinioRegistrationService;

@Component
public class RegistrationService {
    private final static Logger _log = LoggerFactory.getLogger(RegistrationService.class);

    @Autowired
    MinioRegistrationService minioService;

    /*
     * Generic
     */
    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'WRITE')")
    public RegistrationDTO addRegistration(
            String scopeId, String userId,
            String kind, String name, String type)
            throws InvalidArgumentException, SystemException {
        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return RegistrationDTO.fromRegistration(minioService.addBucketRegistration(scopeId, userId, name, type));
        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'DELETE')")
    public RegistrationDTO deleteRegistration(
            String scopeId, String userId,
            String kind, long id)
            throws InvalidArgumentException, SystemException, NoSuchRegistrationException {
        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {

            // fetch to validate scope
            BucketRegistration reg = minioService.getBucketRegistration(id);
            if (!reg.getScopeId().equals(scopeId)) {
                throw new AccessDeniedException("scope does not match");
            }

            return RegistrationDTO.fromRegistration(minioService.deleteBucketRegistration(id));

        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public RegistrationDTO getRegistration(
            String scopeId, String userId,
            String kind, long id)
            throws InvalidArgumentException, SystemException, NoSuchRegistrationException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            // fetch to validate scope
            BucketRegistration reg = minioService.getBucketRegistration(id);
            if (!reg.getScopeId().equals(scopeId)) {
                throw new AccessDeniedException("scope does not match");
            }

            return RegistrationDTO.fromRegistration(reg);

        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public List<RegistrationDTO> listRegistration(
            String scopeId, String userId,
            String kind, String name)
            throws InvalidArgumentException, SystemException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return minioService.listBucketRegistration(scopeId, name).stream()
                    .map(r -> RegistrationDTO.fromRegistration(r)).collect(Collectors.toList());
        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'READ')")
    public long countRegistrations(
            String scopeId, String userId,
            String kind, String name)
            throws InvalidArgumentException, SystemException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return minioService.countBucketRegistration(scopeId, name);
        }

        throw new InvalidArgumentException();
    }

}
