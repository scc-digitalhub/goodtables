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
    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'WRITE')")
    public RegistrationDTO addRegistration(
            String spaceId, String userId,
            String kind, String name, String type)
            throws InvalidArgumentException, SystemException {
        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return RegistrationDTO.fromRegistration(minioService.addBucketRegistration(spaceId, userId, name, type));
        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'DELETE')")
    public RegistrationDTO deleteRegistration(
            String spaceId, String userId,
            String kind, long id)
            throws InvalidArgumentException, SystemException, NoSuchRegistrationException {
        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {

            // fetch to validate space
            BucketRegistration reg = minioService.getBucketRegistration(id);
            if (!reg.getSpaceId().equals(spaceId)) {
                throw new AccessDeniedException("space does not match");
            }

            return RegistrationDTO.fromRegistration(minioService.deleteBucketRegistration(id));

        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public RegistrationDTO getRegistration(
            String spaceId, String userId,
            String kind, long id)
            throws InvalidArgumentException, SystemException, NoSuchRegistrationException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            // fetch to validate space
            BucketRegistration reg = minioService.getBucketRegistration(id);
            if (!reg.getSpaceId().equals(spaceId)) {
                throw new AccessDeniedException("space does not match");
            }

            return RegistrationDTO.fromRegistration(reg);

        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<RegistrationDTO> getRegistrations(
            String spaceId, String userId,
            String kind, long[] ids)
            throws InvalidArgumentException, SystemException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            // filter space
            return minioService.getBucketRegistrations(ids).stream()
                    .filter(r -> spaceId.equals(r.getSpaceId()))
                    .map(r -> RegistrationDTO.fromRegistration(r)).collect(Collectors.toList());
        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<RegistrationDTO> listRegistration(
            String spaceId, String userId,
            String kind, String name)
            throws InvalidArgumentException, SystemException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return minioService.listBucketRegistration(spaceId, name).stream()
                    .map(r -> RegistrationDTO.fromRegistration(r)).collect(Collectors.toList());
        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public long countRegistrations(
            String spaceId, String userId,
            String kind, String name)
            throws InvalidArgumentException, SystemException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return minioService.countBucketRegistration(spaceId, name);
        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public List<RegistrationDTO> listRegistration(
            String spaceId, String userId,
            String kind)
            throws InvalidArgumentException, SystemException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return minioService.listBucketRegistration(spaceId).stream()
                    .map(r -> RegistrationDTO.fromRegistration(r)).collect(Collectors.toList());
        }

        throw new InvalidArgumentException();
    }

    @PreAuthorize("hasPermission(#spaceId, 'SPACE', 'READ')")
    public long countRegistrations(
            String spaceId, String userId,
            String kind)
            throws InvalidArgumentException, SystemException {

        if (RegistrationType.MINIO.equals(RegistrationType.fromString(kind))) {
            return minioService.countBucketRegistration(spaceId);
        }

        throw new InvalidArgumentException();
    }

}
