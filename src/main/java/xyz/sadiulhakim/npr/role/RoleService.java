package xyz.sadiulhakim.npr.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.role.dto.RoleDTO;
import xyz.sadiulhakim.npr.role.event.DeleteRoleEvent;
import xyz.sadiulhakim.npr.role.model.Role;
import xyz.sadiulhakim.npr.role.model.RoleRepo;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    @Value("${default.pagination.size:0}")
    private int paginationSize;

    private final RoleRepo roleRepo;
    private final ApplicationEventPublisher eventPublisher;

    public RoleService(RoleRepo roleRepo, ApplicationEventPublisher eventPublisher) {
        this.roleRepo = roleRepo;
        this.eventPublisher = eventPublisher;
    }

    public void save(Role role) {

        try {

            LOGGER.info("UserService.save :: saving/updating user {}", role.getName());

            Optional<Role> existingRole = getById(role.getId());
            if (existingRole.isEmpty()) {
                roleRepo.save(role);
                return;
            }

            Role exRole = existingRole.get();
            if (StringUtils.hasText(role.getName())) {
                exRole.setName(role.getName());
            }

            if (StringUtils.hasText(role.getDescription())) {
                exRole.setDescription(role.getDescription());
            }

            roleRepo.save(exRole);
        } catch (Exception ex) {
            LOGGER.error("RoleService.save :: {}", ex.getMessage());
        }
    }

    public List<RoleDTO> findAll() {
        return roleRepo.findAll().stream()
                .map(r -> new RoleDTO(r.getId(), r.getName(), r.getDescription()))
                .toList();
    }

    public Optional<Role> getById(long roleId) {

        if (roleId == 0) {
            return Optional.empty();
        }

        Optional<Role> role = roleRepo.findById(roleId);
        if (role.isEmpty()) {
            LOGGER.error("RoleService.getById :: Could not find role {}", roleId);
        }

        return role;
    }

    public Optional<Role> getByName(String name) {

        if (!StringUtils.hasText(name)) {
            return Optional.empty();
        }

        Optional<Role> role = roleRepo.findByName(name);
        if (role.isEmpty()) {
            LOGGER.error("RoleService.getByName :: Could not find role {}", name);
        }

        return role;
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        return findAllPaginatedWithSize(pageNumber, paginationSize);
    }

    public PaginationResult findAllPaginatedWithSize(int pageNumber, int size) {

        LOGGER.info("RoleService.findAllPaginated :: finding role page : {}", pageNumber);
        Page<Role> page = roleRepo.findAll(PageRequest.of(pageNumber, size, Sort.by("name")));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult search(String text, int pageNumber) {

        LOGGER.info("RoleService.search :: search role by text : {}", text);
        Page<Role> page = roleRepo.findByNameContainingOrDescriptionContaining(text, text, PageRequest.of(pageNumber, 200));
        return PageUtil.prepareResult(page);
    }

    @Transactional
    public boolean delete(long id) {

        Optional<Role> role = roleRepo.findById(id);
        if (role.isEmpty()) {
            return false;
        }
        eventPublisher.publishEvent(new DeleteRoleEvent(role.get().getName()));
        return true;
    }

    public void forceDelete(Role role) {
        roleRepo.delete(role);
    }

    public long count() {
        return roleRepo.numberOfRoles();
    }

    public byte[] getCsvData() {
        final int batchSize = 500;
        int batchNumber = 0;
        StringBuilder sb = new StringBuilder("Id,Role,Description\n");
        Page<Role> page;
        do {
            page = roleRepo.findAll(PageRequest.of(batchNumber, batchSize));
            List<Role> users = page.getContent();
            for (Role role : users) {
                sb.append(role.getId())
                        .append(",")
                        .append(role.getName())
                        .append(",")
                        .append(role.getDescription())
                        .append("\n");

            }
            batchNumber++;
        } while (page.hasNext());

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
