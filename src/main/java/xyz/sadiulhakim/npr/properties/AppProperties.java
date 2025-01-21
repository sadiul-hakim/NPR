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

    public int getPaginationSize() {
        return paginationSize;
    }

    public String getDefaultUserPhotoName() {
        return defaultUserPhotoName;
    }

    public String getUserImageFolder() {
        return userImageFolder;
    }
}
