package it.smartcommunitylab.goodtables.model;

import org.springframework.context.ApplicationEvent;

public class BucketNotificationEvent extends ApplicationEvent {

    private static final long serialVersionUID = -6120794015170333863L;

    private String bucket;

    private String key;

    private String type;

    private String notificationId;

    private String topic;

    private String action;

    public BucketNotificationEvent(Object source, String bucket, String key, String type, String notificationId,
            String topic,
            String action) {
        super(source);
        this.bucket = bucket;
        this.key = key;
        this.type = type;
        this.notificationId = notificationId;
        this.topic = topic;
        this.action = action;
    }

    public String getBucket() {
        return bucket;
    }

    public String getKey() {
        return key;
    }

    public String getType() {
        return type;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getTopic() {
        return topic;
    }

    public String getAction() {
        return action;
    }

}
