package xyz.sadiulhakim.npr.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.util.PageUtil;

@Service
public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Value("${default.pagination.size:0}")
    private int paginationSize;

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void save(User user) {
        try {

            LOGGER.info("UserService.save :: saving/updating user {}", user.getRole());
            userRepo.save(user);
        } catch (Exception ex) {
            LOGGER.error("UserService.save :: {}", ex.getMessage());
        }
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        Page<User> page = userRepo.findAll(PageRequest.of(pageNumber, paginationSize, Sort.by("name")));
        return PageUtil.prepareResult(page);
    }
}
