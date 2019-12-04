package it.smartcommunitylab.goodtables.model;

import org.springframework.context.ApplicationEvent;

public class BucketRegistrationEvent extends ApplicationEvent {

    private static final long serialVersionUID = 5464205268480314558L;

    private String bucket;

    private String type;

    private String action;

    public BucketRegistrationEvent(Object source, String bucket, String type, String action) {
        super(source);

        this.bucket = bucket;
        this.type = type;
        this.action = action;
    }

    public String getBucket() {
        return bucket;
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
    }

}
