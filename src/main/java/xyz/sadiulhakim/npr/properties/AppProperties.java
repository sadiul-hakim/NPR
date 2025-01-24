package xyz.sadiulhakim.npr.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Value("${default.pagination.size:0}")
    private int paginationSize;

    @Value("${default.user.image.folder:''}")
    private String userImageFolder;

    @Value("${default.user.image.name:''}")
    private String defaultUserPhotoName;

    @Value("${default.brand.image.folder:''}")
    private String brandImageFolder;

    @Value("${default.brand.image.name:''}")
    private String defaultBrandPhotoName;

    @Value("${default.category.image.folder:''}")
    private String categoryImageFolder;

    @Value("${default.category.image.name:''}")
    private String defaultCategoryPhotoName;

    public int getPaginationSize() {
        return paginationSize;
    }

    public String getDefaultUserPhotoName() {
        return defaultUserPhotoName;
    }

    public String getUserImageFolder() {
        return userImageFolder;
    }

    public String getBrandImageFolder() {
        return brandImageFolder;
    }

    public String getDefaultBrandPhotoName() {
        return defaultBrandPhotoName;
    }

    public String getCategoryImageFolder() {
        return categoryImageFolder;
    }

    public String getDefaultCategoryPhotoName() {
        return defaultCategoryPhotoName;
    }
}
