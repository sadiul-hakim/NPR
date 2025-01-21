package xyz.sadiulhakim.npr.role.event;

import org.springframework.modulith.NamedInterface;

@NamedInterface("delete-role-event")
public record DeleteRoleEvent(
        String role
) {
}
