package it.smartcommunitylab.goodtables.events;

import javax.persistence.PostPersist;
import javax.persistence.PreRemove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.model.BucketNotification;
import it.smartcommunitylab.goodtables.model.BucketNotificationEvent;

@Component
public class BucketNotificationEventListener {
    private final static Logger _log = LoggerFactory.getLogger(BucketNotificationEventListener.class);

    // autowired here does NOT work since JPA listeners are stateless
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public BucketNotificationEventListener(ApplicationEventPublisher bean) {
        this.applicationEventPublisher = bean;
    }

    @PostPersist
    private void postPersist(final BucketNotification bn) {
        _log.debug("postPersist event for " + bn.toString());

        // build an event for spring if all fields populated
        if (!bn.getTopic().isEmpty()) {
            BucketNotificationEvent event = new BucketNotificationEvent(this,
                    bn.getBucket(), "", bn.getType(),
                    bn.getNotificationId(), bn.getTopic(), "create");
            applicationEventPublisher.publishEvent(event);
        }
    }

    @PreRemove
    private void preRemove(final BucketNotification bn) {
        _log.debug("preRemove event for " + bn.toString());

        // build an event for spring if all fields populated
        if (!bn.getTopic().isEmpty()) {
            BucketNotificationEvent event = new BucketNotificationEvent(this,
                    bn.getBucket(), "", bn.getType(),
                    bn.getNotificationId(), bn.getTopic(), "remove");
            applicationEventPublisher.publishEvent(event);
        }

    }

}
