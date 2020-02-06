package it.smartcommunitylab.goodtables.model;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.smartcommunitylab.goodtables.serializer.ValidationResultDeserializer;
import it.smartcommunitylab.goodtables.serializer.ValidationResultSerializer;

@JsonSerialize(using = ValidationResultSerializer.class)
@JsonDeserialize(using = ValidationResultDeserializer.class)
public class ValidationResultDTO {
    private long id;

    private String kind;

    private String name;

    private String key;

    private String type;

    private int status;

    private String report;

    private Date createdDate;

    private String userId;

    private String spaceId;

    public ValidationResultDTO() {
        super();
        id = -1;
        kind = "";
        name = "";
        key = "";
        type = "";
        status = ValidationStatus.UNKNOWN.value();
        report = "";
        createdDate = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

    @Override
    public String toString() {
        return "ValidationResultDTO [id=" + id + ", kind=" + kind + ", name=" + name + ", key=" + key + ", type=" + type
                + ", report=" + report + ", createdDate=" + createdDate + "]";
    }

    /*
     * Builder
     */

    public static ValidationResultDTO fromResult(ValidationResult res) {
        ValidationResultDTO dto = new ValidationResultDTO();
        dto.id = res.getId();
        dto.kind = res.getKind();
        dto.name = res.getName();
        dto.key = res.getKey();
        dto.type = res.getType();
        dto.status = res.getStatus();
        dto.report = res.getReport();
        dto.createdDate = res.getCreatedDate();

        dto.userId = res.getUserId();
        dto.spaceId = res.getSpaceId();

        return dto;
    }

}
