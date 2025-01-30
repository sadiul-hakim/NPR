package xyz.sadiulhakim.npr.category.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.modulith.NamedInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.sadiulhakim.npr.category.event.CategoryEvent;
import xyz.sadiulhakim.npr.event.EntityEventType;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.FileUtil;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@NamedInterface("category-service")
public class CategoryService {

    private final Logger LOGGER = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository categoryRepository;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher eventPublisher;

    CategoryService(CategoryRepository categoryRepository, AppProperties appProperties,
                    ApplicationEventPublisher eventPublisher) {
        this.categoryRepository = categoryRepository;
        this.appProperties = appProperties;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void save(Category category, MultipartFile photo) {

        try {

            LOGGER.info("CategoryService.save :: saving/updating Category {}", category.getName());

            Optional<Category> existingCategory = getById(category.getId());
            if (existingCategory.isEmpty()) {

                if (Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                    category.setPicture(appProperties.getDefaultCategoryPhotoName());
                } else {

                    String fileName = FileUtil.uploadFile(appProperties.getCategoryImageFolder(), photo.getOriginalFilename(), photo.getInputStream());
                    if (fileName.isEmpty()) {
                        category.setPicture(appProperties.getDefaultCategoryPhotoName());
                    } else {
                        category.setPicture(fileName);
                    }
                }

                categoryRepository.save(category);
                eventPublisher.publishEvent(new CategoryEvent(category.getName(), EntityEventType.CREATED));
                return;
            }

            update(existingCategory.get(), category, photo);
        } catch (Exception ex) {
            LOGGER.error("CategoryService.save :: {}", ex.getMessage());
        }
    }

    private void update(Category exCategory, Category category, MultipartFile photo) {
        try {
            if (StringUtils.hasText(category.getName())) {
                exCategory.setName(category.getName());
            }

            if (!Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String fileName = FileUtil.uploadFile(appProperties.getCategoryImageFolder(), photo.getOriginalFilename(),
                        photo.getInputStream());

                if (StringUtils.hasText(fileName) && !exCategory.getPicture()
                        .equals(appProperties.getDefaultCategoryPhotoName())) {

                    boolean deleted = FileUtil.deleteFile(appProperties.getCategoryImageFolder(), exCategory.getPicture());
                    if (deleted) {
                        LOGGER.info("CategoryService.save :: File {} is deleted", exCategory.getPicture());
                    }

                    exCategory.setPicture(fileName);
                }
            }

            categoryRepository.save(exCategory);
        } catch (Exception ex) {
            LOGGER.error("CategoryService.update :: {}", ex.getMessage());
        }
    }

    public Optional<Category> getById(long categoryId) {

        if (categoryId == 0) {
            return Optional.empty();
        }

        Optional<Category> Category = categoryRepository.findById(categoryId);
        if (Category.isEmpty()) {
            LOGGER.error("CategoryService.getById :: Could not find Category {}", categoryId);
        }

        return Category;
    }

    public Optional<Category> getByName(String name) {

        if (!StringUtils.hasText(name)) {
            return Optional.empty();
        }

        Optional<Category> Category = categoryRepository.findByName(name);
        if (Category.isEmpty()) {
            LOGGER.error("CategoryService.getByName :: Could not find Category {}", name);
        }

        return Category;
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        return findAllPaginatedWithSize(pageNumber, appProperties.getPaginationSize());
    }

    public PaginationResult findAllPaginatedWithSize(int pageNumber, int size) {

        LOGGER.info("CategoryService.findAllPaginated :: finding Category page : {}", pageNumber);
        Page<Category> page = categoryRepository.findAll(PageRequest.of(pageNumber, size, Sort.by("name")));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult search(String text, int pageNumber) {

        LOGGER.info("CategoryService.searchUser :: search category by text : {}", text);
        Page<Category> page = categoryRepository.findAllByNameContaining(text, PageRequest.of(pageNumber, 200));
        return PageUtil.prepareResult(page);
    }

    public long count() {
        return categoryRepository.numberOfCategories();
    }

    public byte[] getCsvData() {

        final int batchSize = 500;
        int batchNumber = 0;
        StringBuilder sb = new StringBuilder("Id,Name,Picture\n");
        Page<Category> page;
        do {
            page = categoryRepository.findAll(PageRequest.of(batchNumber, batchSize));
            List<Category> categories = page.getContent();
            for (Category Category : categories) {
                sb.append(Category.getId())
                        .append(",")
                        .append(Category.getName())
                        .append(",")
                        .append(Category.getPicture())
                        .append("\n");
            }
            batchNumber++;
        } while (page.hasNext());

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void forceDelete(Category Category) {
        categoryRepository.delete(Category);
    }

    @Transactional
    public void delete(long CategoryId) {

        Optional<Category> Category = categoryRepository.findById(CategoryId);
        Category.ifPresent(c -> {

            if (!c.getPicture().equals(appProperties.getDefaultCategoryPhotoName())) {
                boolean deleted = FileUtil.deleteFile(appProperties.getCategoryImageFolder(), c.getPicture());
                if (deleted) {
                    LOGGER.info("CategoryService.delete :: deleted file {}", c.getPicture());
                }
            }
            eventPublisher.publishEvent(new CategoryEvent(c.getName(), EntityEventType.DELETED));
        });

    }
}
