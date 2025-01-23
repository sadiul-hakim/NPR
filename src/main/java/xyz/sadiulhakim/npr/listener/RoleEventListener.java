package xyz.sadiulhakim.npr.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.role.RoleService;
import xyz.sadiulhakim.npr.role.event.DeleteRoleEvent;
import xyz.sadiulhakim.npr.user.UserService;

@Component
class RoleEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleEventListener.class);

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

        var role = roleService.getByName(event.role());
        role.ifPresent(roleService::forceDelete);
        LOGGER.info("RoleEventListener :: deleting role {}", event.role());
    }
}
