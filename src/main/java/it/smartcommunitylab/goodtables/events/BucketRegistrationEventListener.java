package it.smartcommunitylab.goodtables.events;

import javax.persistence.PostPersist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.model.BucketRegistration;
import it.smartcommunitylab.goodtables.model.BucketRegistrationEvent;

@Component
public class BucketRegistrationEventListener {
    private final static Logger _log = LoggerFactory.getLogger(BucketRegistrationEventListener.class);

    // autowired here does NOT work since JPA listeners are stateless
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public BucketRegistrationEventListener(ApplicationEventPublisher bean) {
        this.applicationEventPublisher = bean;
    }

    @PostPersist
    private void postPersist(final BucketRegistration reg) {
        _log.debug("postPersist event for " + reg.toString());

        // build an event for spring
        BucketRegistrationEvent event = new BucketRegistrationEvent(this, reg.getBucket(), reg.getType(), "create");
        applicationEventPublisher.publishEvent(event);

    }

}
