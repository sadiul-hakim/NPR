package xyz.sadiulhakim.npr.role.event;

import org.springframework.modulith.NamedInterface;

@NamedInterface("role-delete-event")
public record DeleteRoleEvent(
        String role
) {
}
