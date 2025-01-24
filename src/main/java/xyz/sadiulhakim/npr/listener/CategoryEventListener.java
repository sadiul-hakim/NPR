package xyz.sadiulhakim.npr.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.category.event.CategoryDeleteEvent;
import xyz.sadiulhakim.npr.category.model.CategoryService;

@Component
class CategoryEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(CategoryEventListener.class);
    private final CategoryService categoryService;

    public CategoryEventListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @ApplicationModuleListener
    void brandDeleteEvent(CategoryDeleteEvent event) {

        var category = categoryService.getByName(event.name());
        category.ifPresent(b -> {
            LOGGER.info("CategoryEventListener :: deleting category {}", event.name());
            categoryService.forceDelete(b);
        });
    }
}
