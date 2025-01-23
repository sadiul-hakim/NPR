package xyz.sadiulhakim.npr.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import xyz.sadiulhakim.npr.brand.BrandService;
import xyz.sadiulhakim.npr.brand.event.BrandDeleteEvent;

@Component
class BrandEventListener {

    private final Logger LOGGER = LoggerFactory.getLogger(BrandEventListener.class);

    private final BrandService brandService;

    public BrandEventListener(BrandService brandService) {
        this.brandService = brandService;
    }

    @ApplicationModuleListener
    void brandDeleteEvent(BrandDeleteEvent event) {

        var brand = brandService.getById(event.brandId());
        brand.ifPresent(b -> {
            LOGGER.info("BrandEventListener :: deleting brand {}", event.brandId());
            brandService.forceDelete(b);
        });
    }
}
