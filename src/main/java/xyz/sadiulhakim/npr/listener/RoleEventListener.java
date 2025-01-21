package xyz.sadiulhakim.npr.listener;

import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.role.RoleService;
import xyz.sadiulhakim.npr.role.event.DeleteRoleEvent;
import xyz.sadiulhakim.npr.role.model.Role;
import xyz.sadiulhakim.npr.user.UserService;

import java.util.Optional;

@Component
class RoleEventListener {

    private final UserService userService;
    private final RoleService roleService;

    RoleEventListener(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @ApplicationModuleListener
    void deleteRole(DeleteRoleEvent event) {

        if (userService.countByRole(event.role()) > 0) {
            return;
        }

        Optional<Role> role = roleService.getByName(event.role());
        role.ifPresent(roleService::forceDelete);
    }
}
