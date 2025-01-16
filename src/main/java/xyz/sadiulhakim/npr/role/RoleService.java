package xyz.sadiulhakim.npr.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.user.User;
import xyz.sadiulhakim.npr.user.UserService;
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
    private final UserService userService;

    public RoleService(RoleRepo roleRepo, UserService userService) {
        this.roleRepo = roleRepo;
        this.userService = userService;
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

    public List<Role> findAll() {
        return roleRepo.findAll();
    }

    public Optional<Role> getById(long userId) {

        Optional<Role> user = roleRepo.findById(userId);
        if (user.isEmpty()) {
            LOGGER.error("RoleService.getById :: Could not find role {}", userId);
        }

        return user;
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

    public boolean delete(long id) {

        Optional<Role> role = roleRepo.findById(id);
        if (role.isEmpty()) {
            return false;
        }

        List<User> users = userService.getByRole(role.get());
        if (!users.isEmpty()) {
            return false;
        }

        roleRepo.delete(role.get());
        return true;
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
