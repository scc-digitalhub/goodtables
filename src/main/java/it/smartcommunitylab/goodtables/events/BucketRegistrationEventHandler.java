package it.smartcommunitylab.goodtables.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.minio.MinioException;
import it.smartcommunitylab.goodtables.model.BucketRegistrationEvent;
import it.smartcommunitylab.goodtables.service.minio.MinioRegistrationService;

@Component
public class BucketRegistrationEventHandler implements ApplicationListener<BucketRegistrationEvent> {
    private final static Logger _log = LoggerFactory.getLogger(BucketRegistrationEventHandler.class);

    @Autowired
    MinioRegistrationService service;

    @Override
    public void onApplicationEvent(BucketRegistrationEvent event) {
        _log.debug("handle event " + event.getAction() + " for registration on bucket " + event.getBucket()
                + " with type " + event.getType());
        try {
            switch (event.getAction()) {
            case "create":
                // create notification for new registration
                service.registerBucketNotification(event.getBucket(), event.getType());
                break;
            }
        } catch (MinioException e) {
            // ignore
            _log.error("error handling event: " + e.getMessage());
            e.printStackTrace();
        }

    }
}