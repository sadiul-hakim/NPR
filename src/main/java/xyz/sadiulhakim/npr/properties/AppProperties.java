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

    @Value("${default.product.image.folder:''}")
    private String productImageFolder;

    @Value("${default.product.image.name:''}")
    private String defaultProductPhotoName;

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

    public String getDefaultProductPhotoName() {
        return defaultProductPhotoName;
    }

    public String getProductImageFolder() {
        return productImageFolder;
    }
}
