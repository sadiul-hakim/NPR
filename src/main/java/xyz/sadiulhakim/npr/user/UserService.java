package xyz.sadiulhakim.npr.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.util.DateUtil;
import xyz.sadiulhakim.npr.util.FileUtil;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Value("${default.pagination.size:0}")
    private int paginationSize;

    @Value("${default.user.image.folder:''}")
    public String folder;

    @Value("${default.user.image.name:''}")
    private String defaultPhotoName;

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public Optional<User> getById(long userId) {

        Optional<User> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            LOGGER.error("UserService.getById :: Could not find user {}", userId);
        }

        return user;
    }

    public void save(User user, MultipartFile photo) {

        try {

            LOGGER.info("UserService.save :: saving/updating user {}", user.getRole());

            Optional<User> existingUser = getById(user.getId());
            if (existingUser.isEmpty()) {
                String fileName = FileUtil.uploadFile(folder, photo.getOriginalFilename(), photo.getInputStream());
                if (fileName.isEmpty()) {
                    user.setPicture("default.png");
                } else {
                    user.setPicture(fileName);
                }

                user.setCreatedAt(LocalDateTime.now());
                userRepo.save(user);
                return;
            }

            User exUser = existingUser.get();
            if (StringUtils.hasText(user.getName())) {
                exUser.setName(user.getName());
            }

            if (StringUtils.hasText(user.getEmail())) {
                exUser.setEmail(user.getEmail());
            }

            if (user.getRole() != null) {
                exUser.setRole(user.getRole());
            }

            if (!Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String fileName = FileUtil.uploadFile(folder, photo.getOriginalFilename(), photo.getInputStream());

                if (StringUtils.hasText(fileName)) {
                    boolean deleted = FileUtil.deleteFile(folder, exUser.getPicture());
                    if (deleted) {
                        LOGGER.info("UserService.save :: File {} is deleted", exUser.getPicture());
                    }

                    exUser.setPicture(fileName);
                }
            }

            userRepo.save(exUser);
        } catch (Exception ex) {
            LOGGER.error("UserService.save :: {}", ex.getMessage());
        }
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        return findAllPaginatedWithSize(pageNumber, paginationSize);
    }

    public PaginationResult findAllPaginatedWithSize(int pageNumber, int size) {

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

        Optional<User> user = userRepo.findById(id);
        user.ifPresent(u -> {

            if (!u.getPicture().equals(defaultPhotoName)) {
                boolean deleted = FileUtil.deleteFile(folder, u.getPicture());
                if (deleted) {
                    LOGGER.info("UserService.delete :: deleted file {}", u.getPicture());
                }
            }
            userRepo.delete(u);
        });
    }

    public long count() {
        return userRepo.numberOfUsers();
    }

    public byte[] getCsvData() {
        final int batchSize = 500;
        int batchNumber = 0;
        StringBuilder sb = new StringBuilder("Id,Name,Email,Picture,Date\n");
        Page<User> page;
        do {
            page = userRepo.findAll(PageRequest.of(batchNumber, batchSize));
            List<User> users = page.getContent();
            for (User user : users) {
                sb.append(user.getId())
                        .append(",")
                        .append(user.getName())
                        .append(",")
                        .append(user.getEmail())
                        .append(",")
                        .append(user.getPicture())
                        .append(",")
                        .append(DateUtil.format(user.getCreatedAt()))
                        .append("\n");

            }
            batchNumber++;
        } while (page.hasNext());

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
