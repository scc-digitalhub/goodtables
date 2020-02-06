package it.smartcommunitylab.goodtables.model;

import static javax.persistence.TemporalType.TIMESTAMP;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import it.smartcommunitylab.goodtables.events.BucketRegistrationEventListener;

@Entity
@EntityListeners({ AuditingEntityListener.class, BucketRegistrationEventListener.class })
public class BucketRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String bucket;

    private String type;

    /*
     * Audit
     */
    private String userId;
    private String spaceId;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    @Temporal(TIMESTAMP)
    private Date createdDate;

    @Column(name = "modified_date")
    @LastModifiedDate
    @Temporal(TIMESTAMP)
    private Date modifiedDate;

    @Column(name = "created_by")
    @CreatedBy
    protected String createdBy;

    @Column(name = "modified_by")
    @LastModifiedBy
    protected String lastModifiedBy;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(String spaceId) {
        this.spaceId = spaceId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    @Override
    public String toString() {
        return "BucketRegistration [id=" + id + ", bucket=" + bucket + ", type=" + type + ", userId=" + userId
                + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", createdBy=" + createdBy
                + ", lastModifiedBy=" + lastModifiedBy + "]";
    }

}
