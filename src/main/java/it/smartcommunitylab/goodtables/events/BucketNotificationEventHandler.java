package it.smartcommunitylab.goodtables.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import it.smartcommunitylab.goodtables.model.BucketNotificationEvent;
import it.smartcommunitylab.goodtables.mqtt.MqttBridge;
import it.smartcommunitylab.goodtables.service.ValidationService;

@Component
public class BucketNotificationEventHandler implements ApplicationListener<BucketNotificationEvent> {
    private final static Logger _log = LoggerFactory.getLogger(BucketNotificationEventHandler.class);

    @Autowired
    ValidationService service;

    @Autowired
    MqttBridge mqtt;

    @Override
    public void onApplicationEvent(BucketNotificationEvent event) {
        _log.debug("handle event " + event.getAction() + " for notification on bucket " + event.getBucket()
                + " with type " + event.getType());
        try {
            switch (event.getAction()) {
            case "create":
                // connect mqtt broker to new topic
                mqtt.subscribe(event);
                break;
            case "remove":
                // disconnect mqtt broker from topic
                mqtt.unsubscribe(event);
                break;
            case "trigger":
                // trigger validation
                service.executeValidation("minio", event.getBucket(), event.getKey(), event.getType());
                break;
            }
        } catch (Exception e) {
            // ignore
            _log.error("error handling event: " + e.getMessage());
            e.printStackTrace();
        }

    }
}