package xyz.sadiulhakim.npr.role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.util.List;

@Service
public class RoleService {

    private final Logger LOGGER = LoggerFactory.getLogger(RoleService.class);

    @Value("${default.pagination.size:0}")
    private int paginationSize;

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

    public PaginationResult findAllPaginated(int pageNumber) {
        Page<Role> page = roleRepo.findAll(PageRequest.of(pageNumber, paginationSize, Sort.by("role")));
        return PageUtil.prepareResult(page);
    }

    public List<Role> findAll() {
        return roleRepo.findAll();
    }
}
