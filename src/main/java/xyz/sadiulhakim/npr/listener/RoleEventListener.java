package xyz.sadiulhakim.npr.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.role.event.DeleteRoleEvent;
import xyz.sadiulhakim.npr.role.model.RoleService;
import xyz.sadiulhakim.npr.user.model.UserService;

@Component
class RoleEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleEventListener.class);

    private final UserService userService;
    private final RoleService roleService;

    RoleEventListener(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Async("defaultTaskExecutor")
    @EventListener
    void deleteRole(DeleteRoleEvent event) {

        if (userService.countByRole(event.role()) > 0) {
            return;
        }

        var role = roleService.getById(event.role());
        role.ifPresent(roleService::forceDelete);
        LOGGER.info("RoleEventListener :: deleting role {}", event.role());
    }
}
