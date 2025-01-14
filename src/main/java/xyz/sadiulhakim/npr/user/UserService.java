package xyz.sadiulhakim.npr.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Value("${default.pagination.size:0}")
    private int paginationSize;

    @Value("${default.user.image.folder:''}")
    private String filePath;

    @Value("${default.user.image.name:''}")
    private String defaultPhotoName;

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void save(User user, MultipartFile photo) {
        try {

            LOGGER.info("UserService.save :: saving/updating user {}", user.getRole());

            if (photo.isEmpty()) {
                user.setPicture("default.png");
            }

            user.setCreatedAt(LocalDateTime.now());
            userRepo.save(user);
        } catch (Exception ex) {
            LOGGER.error("UserService.save :: {}", ex.getMessage());
        }
    }

    public PaginationResult findAllPaginated(int pageNumber) {

        LOGGER.info("UserService.findAllPaginated :: finding user page : {}", pageNumber);
        Page<User> page = userRepo.findAll(PageRequest.of(pageNumber, paginationSize, Sort.by("name")));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult searchUser(String text, int pageNumber) {

        LOGGER.info("UserService.searchUser :: search user by text : {}", text);
        Page<User> page = userRepo.findByNameContainingOrEmailContaining(text, text, PageRequest.of(pageNumber, 200));
        return PageUtil.prepareResult(page);
    }

    public void delete(long id) {
        userRepo.deleteById(id);
    }
}
