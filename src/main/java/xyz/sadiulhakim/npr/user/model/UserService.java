package xyz.sadiulhakim.npr.user.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.DateUtil;
import xyz.sadiulhakim.npr.util.FileUtil;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@NamedInterface("user-service")
public class UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepo userRepo;
    private final AppProperties appProperties;
    private final PasswordEncoder passwordEncoder;

    UserService(UserRepo userRepo, AppProperties appProperties, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.appProperties = appProperties;
        this.passwordEncoder = passwordEncoder;
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }

    public long countByRole(String role) {
        return userRepo.countByRole(role);
    }

    public Optional<User> getById(long userId) {

        if (userId == 0) {
            return Optional.empty();
        }

        Optional<User> user = userRepo.findById(userId);
        if (user.isEmpty()) {
            LOGGER.error("UserService.getById :: Could not find user {}", userId);
        }

        return user;
    }

    public void save(User user, MultipartFile photo) {

        try {

            LOGGER.info("UserService.save :: saving/updating user {}", user.getName());

            Optional<User> existingUser = getById(user.getId());
            if (existingUser.isEmpty()) {

                if (Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                    user.setPicture(appProperties.getDefaultUserPhotoName());
                } else {

                    String fileName = FileUtil.uploadFile(appProperties.getUserImageFolder(), photo.getOriginalFilename(), photo.getInputStream());
                    if (fileName.isEmpty()) {
                        user.setPicture(appProperties.getDefaultUserPhotoName());
                    } else {
                        user.setPicture(fileName);
                    }
                }

                user.setCreatedAt(LocalDateTime.now());
                user.setPassword(passwordEncoder.encode(user.getPassword()));

                userRepo.save(user);
                return;
            }

            update(existingUser.get(), user, photo);
        } catch (Exception ex) {
            LOGGER.error("UserService.save :: {}", ex.getMessage());
        }
    }

    private void update(User exUser, User user, MultipartFile photo) throws IOException {

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
            String fileName = FileUtil.uploadFile(appProperties.getUserImageFolder(), photo.getOriginalFilename(), photo.getInputStream());

            if (StringUtils.hasText(fileName) && !exUser.getPicture().equals(appProperties.getDefaultUserPhotoName())) {
                boolean deleted = FileUtil.deleteFile(appProperties.getUserImageFolder(), exUser.getPicture());
                if (deleted) {
                    LOGGER.info("UserService.update :: File {} is deleted", exUser.getPicture());
                }
            }

            if (StringUtils.hasText(fileName)) {
                exUser.setPicture(fileName);
            }
        }

        userRepo.save(exUser);
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        return findAllPaginatedWithSize(pageNumber, appProperties.getPaginationSize());
    }

    public PaginationResult findAllPaginatedWithSize(int pageNumber, int size) {

        LOGGER.info("UserService.findAllPaginated :: finding user page : {}", pageNumber);
        Page<User> page = userRepo.findAll(PageRequest.of(pageNumber, size, Sort.by("name")));
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

            if (!u.getPicture().equals(appProperties.getDefaultUserPhotoName())) {
                boolean deleted = FileUtil.deleteFile(appProperties.getUserImageFolder(), u.getPicture());
                if (deleted) {
                    LOGGER.info("UserService.delete :: deleted file {}", u.getPicture());
                }
            }
            userRepo.delete(u);
        });
    }

    public String changePassword(PasswordDTO passwordDTO, long userId) {

        try {
            Optional<User> user = getById(userId);
            if (user.isEmpty()) {
                return "User does not exist!";
            }

            if (!StringUtils.hasText(passwordDTO.getCurrentPassword()) ||
                    !StringUtils.hasText(passwordDTO.getNewPassword()) ||
                    !StringUtils.hasText(passwordDTO.getConfirmPassword())) {
                return "Password can not be empty!";
            }

            if (!passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
                return "Password Does not Match!";
            }

            User exUser = user.get();
            if (passwordEncoder.matches(passwordDTO.getNewPassword(), exUser.getPassword())) {
                return "Invalid Password!";
            }

            exUser.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            userRepo.save(exUser);

            return "Password is reset successfully!";
        } catch (Exception ex) {
            LOGGER.error("UserService.changePassword :: {}", ex.getMessage());
            return "Could not reset password, Please try again!";
        }
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
