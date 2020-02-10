package it.smartcommunitylab.goodtables.security;

import java.util.List;

public interface RoleActionConverter {

    public List<String> toRole(String action);

    public List<String> toActions(String role);
}
