package it.smartcommunitylab.goodtables.model;

public class RegistrationDTO {
    private long id;
    private String kind;
    private String name;
    private String type;

    private String userId;
    private String spaceId;

    public RegistrationDTO() {
        id = -1;
        kind = "";
        name = "";
        type = "";

        userId = "";
        spaceId = "";
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

    public static RegistrationDTO fromRegistration(BucketRegistration br) {
        RegistrationDTO reg = new RegistrationDTO();
        reg.id = br.getId();
        reg.kind = RegistrationType.MINIO.toString();
        reg.name = br.getBucket();
        reg.type = br.getType();

        // audit
        reg.userId = br.getUserId();
        reg.spaceId = br.getSpaceId();

        return reg;
    }
}
