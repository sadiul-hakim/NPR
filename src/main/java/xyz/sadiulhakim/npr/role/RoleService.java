package xyz.sadiulhakim.npr.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    private final RoleRepo roleRepo;

    public RoleService(RoleRepo roleRepo) {
        this.roleRepo = roleRepo;
    }

    public void save(Role role) {
        try {

            LOGGER.info("RoleService.save :: saving/updating role {}", role.getRole());
            roleRepo.save(role);
        } catch (Exception ex) {
            LOGGER.error("RoleService.save :: {}", ex.getMessage());
        }
    }
}
