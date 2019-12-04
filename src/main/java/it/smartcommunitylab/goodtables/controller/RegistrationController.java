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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.smartcommunitylab.goodtables.common.InvalidArgumentException;
import it.smartcommunitylab.goodtables.common.NoSuchRegistrationException;
import it.smartcommunitylab.goodtables.common.SystemException;
import it.smartcommunitylab.goodtables.model.RegistrationDTO;
import it.smartcommunitylab.goodtables.model.RegistrationType;
import it.smartcommunitylab.goodtables.model.ValidationType;
import it.smartcommunitylab.goodtables.service.RegistrationService;
import it.smartcommunitylab.goodtables.util.ControllerUtil;

@RestController
public class RegistrationController {
    private final static Logger _log = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    RegistrationService service;

    @Value("${scopes.default}")
    private String defaultScope;

    /*
     * Registrations w/scope
     */

    @GetMapping(value = "/api/c/{scope}/registration/{kind}/{name}", produces = "application/json")
    @ResponseBody
    public List<RegistrationDTO> list(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            HttpServletRequest request, HttpServletResponse response,
            Pageable pageable) throws SystemException, InvalidArgumentException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        _log.debug("list events for " + kind + " name " + name + " by " + userId + " for scope " + scopeId);

        long total = service.countRegistrations(scopeId, userId, kind, name);
        List<RegistrationDTO> list = service.listRegistration(scopeId, userId, kind, name);

        // add total count as header
        response.setHeader("X-Total-Count", String.valueOf(total));

        return list;
    }

    @PostMapping(value = "/api/c/{scope}/registration/{kind}/{name}", produces = "application/json")
    @ResponseBody
    public RegistrationDTO add(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @RequestBody RegistrationDTO reg,
            HttpServletRequest request, HttpServletResponse response)
            throws InvalidArgumentException, SystemException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        // override values in body with path values
        reg.setKind(kind);
        reg.setName(name);

        // try to fetch type, will trigger exception if not supported
        RegistrationType regType = RegistrationType.fromString(kind);
        ValidationType valType = ValidationType.fromString(reg.getType());

        _log.debug("add registration for " + regType.toString() + " name " + name + " for type " + valType.toString()
                + " by " + userId + " for scope " + scopeId);

        // add via service
        return service.addRegistration(scopeId, userId, reg.getKind(), reg.getName(), valType.toString());

    }

    @GetMapping(value = "/api/c/{scope}/registration/{kind}/{name}/{id}", produces = "application/json")
    @ResponseBody
    public RegistrationDTO get(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchRegistrationException, SystemException, InvalidArgumentException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        _log.debug("get registration " + String.valueOf(id) + " for " + kind
                + " by " + userId + " for scope " + scopeId);

        // will trigger exception if not found
        return service.getRegistration(scopeId, userId, kind, id);
    }

    @DeleteMapping(value = "/api/c/{scope}/registration/{kind}/{name}/{id}", produces = "application/json")
    @ResponseBody
    public RegistrationDTO delete(
            @PathVariable("scope") Optional<String> scope,
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchRegistrationException, SystemException, InvalidArgumentException {

        String scopeId = scope.orElse(defaultScope);
        String userId = ControllerUtil.getUserId(request);

        _log.debug("delete registration " + String.valueOf(id) + " for " + kind
                + " by " + userId + " for scope " + scopeId);

        // will trigger exception if not found
        return service.deleteRegistration(scopeId, userId, kind, id);
    }

    /*
     * Registrations
     */

    @GetMapping(value = "/api/registration/{kind}/{name}", produces = "application/json")
    @ResponseBody
    public List<RegistrationDTO> list(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            HttpServletRequest request, HttpServletResponse response,
            Pageable pageable) throws SystemException, InvalidArgumentException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return list(scopeId, kind, name, request, response, pageable);
    }

    @PostMapping(value = "/api/registration/{kind}/{name}", produces = "application/json")
    @ResponseBody
    public RegistrationDTO add(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @RequestBody RegistrationDTO reg,
            HttpServletRequest request, HttpServletResponse response)
            throws InvalidArgumentException, SystemException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return add(scopeId, kind, name, reg, request, response);

    }

    @GetMapping(value = "/api/registration/{kind}/{name}/{id}", produces = "application/json")
    @ResponseBody
    public RegistrationDTO get(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchRegistrationException, SystemException, InvalidArgumentException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return get(scopeId, kind, name, id, request, response);
    }

    @DeleteMapping(value = "/api/registration/{kind}/{name}/{id}", produces = "application/json")
    @ResponseBody
    public RegistrationDTO delete(
            @PathVariable("kind") String kind,
            @PathVariable("name") String name,
            @PathVariable("id") long id,
            HttpServletRequest request, HttpServletResponse response)
            throws NoSuchRegistrationException, SystemException, InvalidArgumentException {

        Optional<String> scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request));
        return delete(scopeId, kind, name, id, request, response);
    }

    /*
     * Types
     */
    @PreAuthorize("hasPermission(#scopeId, 'SCOPE', 'ACCESS')")
    @GetMapping(value = "/api/c/{scope}/registration/types", produces = "application/json")
    @ResponseBody
    public String[] getTypes(
            @PathVariable("scope") String scopeId,
            HttpServletRequest request, HttpServletResponse response)
            throws SystemException {
        _log.debug("get types for scope " + scopeId);
        // list all registration types as valid extensions
        return Arrays.stream(RegistrationType.values()).map(t -> t.toString()).toArray(String[]::new);

    }

    @GetMapping(value = "/api/registration/types", produces = "application/json")
    @ResponseBody
    public String[] getTypes(
            HttpServletRequest request, HttpServletResponse response)
            throws SystemException {
        String scopeId = Optional.ofNullable(ControllerUtil.getScopeId(request)).orElse(defaultScope);
        return getTypes(scopeId, request, response);

    }

    /*
     * Exceptions
     */

    @ExceptionHandler(NoSuchRegistrationException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String notFound(NoSuchRegistrationException ex) {
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
