package it.smartcommunitylab.goodtables.model;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import it.smartcommunitylab.goodtables.events.BucketNotificationEventListener;

@Entity
@EntityListeners({ BucketNotificationEventListener.class })
public class BucketNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String bucket;

    private String type;

    private String notificationId;

    private String topic;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "BucketNotification [id=" + id + ", bucket=" + bucket + ", type=" + type + ", notificationId="
                + notificationId + ", topic=" + topic + "]";
    }

}
