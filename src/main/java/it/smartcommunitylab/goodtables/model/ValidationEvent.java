package it.smartcommunitylab.goodtables.model;

import org.springframework.context.ApplicationEvent;

public class ValidationEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1222169711959406422L;

    private String bucket;

    private String type;

    private String topic;

    public ValidationEvent(Object source, String bucket, String type, String topic) {
        super(source);
        this.bucket = bucket;
        this.type = type;
        this.topic = topic;
    }

    public String getBucket() {
        return bucket;
    }

    public String getType() {
        return type;
    }

    public String getTopic() {
        return topic;
    }

}
