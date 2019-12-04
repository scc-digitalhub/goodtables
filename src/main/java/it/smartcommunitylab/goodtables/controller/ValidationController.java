package it.smartcommunitylab.goodtables.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;
import it.smartcommunitylab.goodtables.common.NoSuchValidationResultException;
import it.smartcommunitylab.goodtables.common.SystemException;
import it.smartcommunitylab.goodtables.model.ValidationResultDTO;
import it.smartcommunitylab.goodtables.model.ValidationType;
import it.smartcommunitylab.goodtables.service.ValidationService;
import it.smartcommunitylab.goodtables.util.ControllerUtil;

@RestController
public class ValidationController {
    private final static Logger _log = LoggerFactory.getLogger(ValidationController.class);

    @Autowired
    ValidationService service;

    @Value("${scopes.default}")
    private String defaultScope;

    /*
     * Results w/scope
     */

    @GetMapping(value = "/api/c/{scope}/validation/{kind}/{name}/{key}", produces = "application/json")
    @ResponseBody
    public List<ValidationResultDTO> listByKey(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("key") String key,
            HttpServletRequest request, HttpServletResponse response,
            Pageable pageable) throws SystemException, InvalidArgumentException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        _log.debug("list results for " + kind + " name " + name + " key " + key
                + " by " + userId + " for scope " + scopeId);

        long total = service.countResult(scopeId, userId, kind, name, key);
        List<ValidationResultDTO> list = service.listResult(scopeId, userId, kind, name, key);

        // add total count as header
        response.setHeader("X-Total-Count", String.valueOf(total));

        return list;
    }

    @GetMapping(value = "/api/c/{scope}/validation/{kind}/{name}", produces = "application/json")
    @ResponseBody
    public List<ValidationResultDTO> listByName(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            HttpServletRequest request, HttpServletResponse response,
            Pageable pageable) throws SystemException, InvalidArgumentException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        _log.debug("list results for " + kind + " name " + name
                + " by " + userId + " for scope " + scopeId);

        long total = service.countResult(scopeId, userId, kind, name);
        List<ValidationResultDTO> list = service.listResult(scopeId, userId, kind, name);

        // add total count as header
        response.setHeader("X-Total-Count", String.valueOf(total));

        return list;
    }

    @GetMapping(value = "/api/c/{scope}/validation/{kind}/{name}/{key}/{id}", produces = "application/json")
    @ResponseBody
    public ValidationResultDTO get(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("key") String key,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchValidationResultException, SystemException, InvalidArgumentException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        _log.debug("get registration " + String.valueOf(id) + " for " + kind + " name " + name + " key " + key
                + " by " + userId + " for scope " + scopeId);

        // will trigger exception if not found
        return service.getResult(scopeId, userId, id);
    }

    @DeleteMapping(value = "/api/c/{scope}/validation/{kind}/{name}/{key}/{id}", produces = "application/json")
    @ResponseBody
    public ValidationResultDTO delete(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("key") String key,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchValidationResultException, SystemException, InvalidArgumentException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        _log.debug("delete registration " + String.valueOf(id) + " for " + kind + " name " + name + " key " + key
                + " by " + userId + " for scope " + scopeId);

        // will trigger exception if not found
        return service.deleteResult(scopeId, userId, id);
    }

    /*
     * Results
     */

    @GetMapping(value = "/api/validation/{kind}/{name}/{key}", produces = "application/json")
    @ResponseBody
    public List<ValidationResultDTO> listByKey(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("key") String key,
            HttpServletRequest request, HttpServletResponse response,
            Pageable pageable) throws SystemException, InvalidArgumentException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return listByKey(scopeId, kind, name, key, request, response, pageable);
    }

    @GetMapping(value = "/api/validation/{kind}/{name}", produces = "application/json")
    @ResponseBody
    public List<ValidationResultDTO> listByName(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            HttpServletRequest request, HttpServletResponse response,
            Pageable pageable) throws SystemException, InvalidArgumentException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return listByName(scopeId, kind, name, request, response, pageable);
    }

    @GetMapping(value = "/api/validation/{kind}/{name}/{key}/{id}", produces = "application/json")
    @ResponseBody
    public ValidationResultDTO get(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("key") String key,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchValidationResultException, SystemException, InvalidArgumentException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return get(scopeId, kind, name, key, id, request, response);
    }

    @DeleteMapping(value = "/api/validation/{kind}/{name}/{key}/{id}", produces = "application/json")
    @ResponseBody
    public ValidationResultDTO delete(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("key") String key,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchValidationResultException, SystemException, InvalidArgumentException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return delete(scopeId, kind, name, key, id, request, response);
    }

    /*
     * Types
     */
    @GetMapping(value = "/api/c/{scope}/validation/types", produces = "application/json")
    @ResponseBody
    public String[] get(
            @PathVariable("scope") Optional<String> scope,
            HttpServletRequest request, HttpServletResponse response)
            throws SystemException {
        // list all validation types as valid extensions
        return Arrays.stream(ValidationType.values()).map(t -> t.toString()).toArray(String[]::new);

    }

    @GetMapping(value = "/api/validation/types", produces = "application/json")
    @ResponseBody
    public String[] get(
            HttpServletRequest request, HttpServletResponse response)
            throws SystemException {
        // list all validation types as valid extensions
        return Arrays.stream(ValidationType.values()).map(t -> t.toString()).toArray(String[]::new);

    }
    /*
     * Exceptions
     */

    @ExceptionHandler(NoSuchValidationResultException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String notFound(NoSuchValidationResultException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(InvalidArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String invalidRequest(InvalidArgumentException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String systemError(SystemException ex) {
        return ex.getMessage();
    }
}
