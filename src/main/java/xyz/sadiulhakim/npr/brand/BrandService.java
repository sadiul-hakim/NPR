package xyz.sadiulhakim.npr.brand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import xyz.sadiulhakim.npr.brand.event.BrandDeleteEvent;
import xyz.sadiulhakim.npr.brand.model.Brand;
import xyz.sadiulhakim.npr.brand.model.BrandRepository;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Service
public class BrandService {

    private final Logger LOGGER = LoggerFactory.getLogger(BrandService.class);
    private final BrandRepository brandRepository;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher eventPublisher;

    public BrandService(BrandRepository brandRepository, AppProperties appProperties, ApplicationEventPublisher eventPublisher) {
        this.brandRepository = brandRepository;
        this.appProperties = appProperties;
        this.eventPublisher = eventPublisher;
    }

    public Optional<Brand> getById(long brandId) {

        if (brandId == 0) {
            return Optional.empty();
        }

        Optional<Brand> brand = brandRepository.findById(brandId);
        if (brand.isEmpty()) {
            LOGGER.error("BrandService.getById :: Could not find brand {}", brandId);
        }

        return brand;
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        return findAllPaginatedWithSize(pageNumber, appProperties.getPaginationSize());
    }

    public PaginationResult findAllPaginatedWithSize(int pageNumber, int size) {

        LOGGER.info("BrandService.findAllPaginated :: finding brand page : {}", pageNumber);
        Page<Brand> page = brandRepository.findAll(PageRequest.of(pageNumber, size, Sort.by("name")));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult search(String text, int pageNumber) {

        LOGGER.info("BrandService.searchUser :: search user by text : {}", text);
        Page<Brand> page = brandRepository.findAllByNameContaining(text, PageRequest.of(pageNumber, 200));
        return PageUtil.prepareResult(page);
    }

    public long count() {
        return brandRepository.numberOfBrands();
    }

    public byte[] getCsvData() {

        final int batchSize = 500;
        int batchNumber = 0;
        StringBuilder sb = new StringBuilder("Id,Name,Picture\n");
        Page<Brand> page;
        do {
            page = brandRepository.findAll(PageRequest.of(batchNumber, batchSize));
            List<Brand> brands = page.getContent();
            for (Brand brand : brands) {
                sb.append(brand.getId())
                        .append(",")
                        .append(brand.getName())
                        .append(",")
                        .append(brand.getPicture())
                        .append("\n");
            }
            batchNumber++;
        } while (page.hasNext());

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void forceDelete(Brand brand) {
        brandRepository.delete(brand);
    }

    public void delete(long brandId) {
        eventPublisher.publishEvent(new BrandDeleteEvent(brandId));
    }
}
