package xyz.sadiulhakim.npr.brand.model;

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
import xyz.sadiulhakim.npr.brand.event.BrandEvent;
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
@NamedInterface("brand-service")
public class BrandService {

    private final Logger LOGGER = LoggerFactory.getLogger(BrandService.class);
    private final BrandRepository brandRepository;
    private final AppProperties appProperties;
    private final ApplicationEventPublisher eventPublisher;

    BrandService(BrandRepository brandRepository, AppProperties appProperties, ApplicationEventPublisher eventPublisher) {
        this.brandRepository = brandRepository;
        this.appProperties = appProperties;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void save(Brand brand, MultipartFile photo) {

        try {

            LOGGER.info("BrandService.save :: saving/updating brand {}", brand.getName());

            Optional<Brand> existingBrand = getById(brand.getId());
            if (existingBrand.isEmpty()) {

                if (Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                    brand.setPicture(appProperties.getDefaultBrandPhotoName());
                } else {

                    String fileName = FileUtil.uploadFile(appProperties.getBrandImageFolder(), photo.getOriginalFilename(), photo.getInputStream());
                    if (fileName.isEmpty()) {
                        brand.setPicture(appProperties.getDefaultBrandPhotoName());
                    } else {
                        brand.setPicture(fileName);
                    }
                }

                brandRepository.save(brand);
                eventPublisher.publishEvent(new BrandEvent(brand.getName(), EntityEventType.CREATED));
                return;
            }

            updateBrand(existingBrand.get(), brand, photo);
        } catch (Exception ex) {
            LOGGER.error("BrandService.save :: {}", ex.getMessage());
        }
    }

    private void updateBrand(Brand exBrand, Brand brand, MultipartFile photo) {

        try {
            if (StringUtils.hasText(brand.getName())) {
                exBrand.setName(brand.getName());
            }

            if (!Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                String fileName = FileUtil.uploadFile(appProperties.getBrandImageFolder(), photo.getOriginalFilename(), photo.getInputStream());

                if (StringUtils.hasText(fileName) && !exBrand.getPicture().equals(appProperties.getDefaultBrandPhotoName())) {
                    boolean deleted = FileUtil.deleteFile(appProperties.getBrandImageFolder(), exBrand.getPicture());
                    if (deleted) {
                        LOGGER.info("BrandService.save :: File {} is deleted", exBrand.getPicture());
                    }

                    exBrand.setPicture(fileName);
                }
            }

            brandRepository.save(exBrand);
        } catch (Exception ex) {
            LOGGER.error("BrandService.updateBrand :: {}", ex.getMessage());
        }
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

    public Optional<Brand> getByName(String name) {

        if (!StringUtils.hasText(name)) {
            return Optional.empty();
        }

        Optional<Brand> brand = brandRepository.findByName(name);
        if (brand.isEmpty()) {
            LOGGER.error("BrandService.getByName :: Could not find brand {}", name);
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

        LOGGER.info("BrandService.searchUser :: search brand by text : {}", text);
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

    @Transactional
    public void delete(long brandId) {

        Optional<Brand> brand = brandRepository.findById(brandId);
        brand.ifPresent(b -> {

            if (!b.getPicture().equals(appProperties.getDefaultBrandPhotoName())) {
                boolean deleted = FileUtil.deleteFile(appProperties.getBrandImageFolder(), b.getPicture());
                if (deleted) {
                    LOGGER.info("BrandService.delete :: deleted file {}", b.getPicture());
                }
            }
            eventPublisher.publishEvent(new BrandEvent(b.getName(), EntityEventType.DELETED));
        });

    }
}
