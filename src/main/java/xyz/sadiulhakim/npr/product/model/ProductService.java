package xyz.sadiulhakim.npr.product.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import xyz.sadiulhakim.npr.event.EntityEventType;
import xyz.sadiulhakim.npr.pojo.PaginationResult;
import xyz.sadiulhakim.npr.product.event.ProductEvent;
import xyz.sadiulhakim.npr.properties.AppProperties;
import xyz.sadiulhakim.npr.util.FileUtil;
import xyz.sadiulhakim.npr.util.PageUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProductService {

    private final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final AppProperties appProperties;
    private final ObjectMapper mapper;

    ProductService(ProductRepository productRepository, ApplicationEventPublisher eventPublisher,
                   AppProperties appProperties, ObjectMapper mapper) {
        this.productRepository = productRepository;
        this.eventPublisher = eventPublisher;
        this.appProperties = appProperties;
        this.mapper = mapper;
    }

    public void save(Product product, MultipartFile photo) {

        try {

            LOGGER.info("Product.save :: saving/updating product {}", product.getName());

            Optional<Product> existingProduct = getById(product.getId());
            if (existingProduct.isEmpty()) {

                if (Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
                    product.setPicture(appProperties.getDefaultProductPhotoName());
                } else {

                    String fileName = FileUtil.uploadFile(appProperties.getProductImageFolder(),
                            photo.getOriginalFilename(), photo.getInputStream());
                    if (fileName.isEmpty()) {
                        product.setPicture(appProperties.getDefaultProductPhotoName());
                    } else {
                        product.setPicture(fileName);
                    }
                }

                product.getDetails().put("name", product.getName());
                productRepository.save(product);
                return;
            }

            update(existingProduct.get(), product, photo);
        } catch (Exception ex) {
            LOGGER.error("ProductService.save :: {}", ex.getMessage());
        }
    }

    private void update(Product exProduct, Product product, MultipartFile photo) throws IOException {

        if (StringUtils.hasText(product.getName())) {
            exProduct.setName(product.getName());
        }

        if (product.getBrand() != null) {
            exProduct.setBrand(product.getBrand());
        }

        if (product.getCategory() != null) {
            exProduct.setCategory(product.getCategory());
        }

        if (StringUtils.hasText(product.getDescription())) {
            exProduct.setDescription(product.getDescription());
        }

        // Updating of Product QRCode , Details , Rating and Reviews would be handled somewhere else.

        if (!Objects.requireNonNull(photo.getOriginalFilename()).isEmpty()) {
            String fileName = FileUtil.uploadFile(appProperties.getProductImageFolder(), photo.getOriginalFilename(),
                    photo.getInputStream());

            if (StringUtils.hasText(fileName) && !exProduct.getPicture().equals(appProperties.getDefaultProductPhotoName())) {
                boolean deleted = FileUtil.deleteFile(appProperties.getUserImageFolder(), exProduct.getPicture());
                if (deleted) {
                    LOGGER.info("ProductService.update :: File {} is deleted", exProduct.getPicture());
                }
            }

            if (StringUtils.hasText(fileName)) {
                exProduct.setPicture(fileName);
            }
        }

        productRepository.save(exProduct);
    }

    public long count() {
        return productRepository.numberOfProducts();
    }

    public Optional<Product> getById(long productId) {

        if (productId == 0) {
            return Optional.empty();
        }

        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) {
            LOGGER.error("ProductService.getById :: Could not find product {}", productId);
        }

        return product;
    }

    public void addDetail(String key, String value, long productId) {

        Optional<Product> product = getById(productId);
        if (product.isEmpty())
            return;

        Product exProduct = product.get();
        exProduct.getDetails().put(key, value);
        productRepository.save(exProduct);
    }

    public void deleteDetail(String key, long productId) {

        Optional<Product> product = getById(productId);
        if (product.isEmpty())
            return;

        Product exProduct = product.get();
        exProduct.getDetails().remove(key);
        productRepository.save(exProduct);
    }

    public Optional<Product> getByName(String name) {

        if (!StringUtils.hasText(name)) {
            return Optional.empty();
        }

        Optional<Product> product = productRepository.findByName(name);
        if (product.isEmpty()) {
            LOGGER.error("ProductService.getByName :: Could not find product {}", name);
        }

        return product;
    }

    public PaginationResult findAllPaginated(int pageNumber) {
        return findAllPaginatedWithSize(pageNumber, appProperties.getPaginationSize());
    }

    public PaginationResult findAllPaginatedWithSize(int pageNumber, int size) {

        LOGGER.info("ProductService.findAllPaginated :: finding products page : {}", pageNumber);
        Page<Product> page = productRepository.findAll(PageRequest.of(pageNumber, size, Sort.by("name")));
        return PageUtil.prepareResult(page);
    }

    public PaginationResult search(String text, int pageNumber) {

        LOGGER.info("ProductService.search :: search product by text : {}", text);
        Page<Product> page = productRepository.findAllByNameContainingOrDescriptionContaining(
                text, text, text, text, PageRequest.of(pageNumber, 200)
        );
        return PageUtil.prepareResult(page);
    }

    public byte[] getCsvData() {
        final int batchSize = 500;
        int batchNumber = 0;
        StringBuilder sb = new StringBuilder("Id,Name,Brand,Category,Picture,QRCode,Description,Details,Rating\n");

        try {

            Page<Product> page;
            do {
                page = productRepository.findAll(PageRequest.of(batchNumber, batchSize));
                List<Product> products = page.getContent();
                for (Product product : products) {
                    sb.append(product.getId())
                            .append(",")
                            .append(product.getName())
                            .append(",")
                            .append(product.getBrand().getName())
                            .append(",")
                            .append(product.getCategory().getName())
                            .append(",")
                            .append(product.getPicture())
                            .append(",")
                            .append(product.getQrCode())
                            .append(",")
                            .append(product.getDescription())
                            .append(",")
                            .append(mapper.writeValueAsString(product.getDetails()))
                            .append(",")
                            .append(product.getRating())
                            .append("\n");
                }
                batchNumber++;
            } while (page.hasNext());
        } catch (Exception ex) {
            LOGGER.info("ProductService.getCSVData :: error occurred {}", ex.getMessage());
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    public void delete(long brandId) {

        Optional<Product> product = productRepository.findById(brandId);
        product.ifPresent(p -> {

            if (!p.getPicture().equals(appProperties.getDefaultProductPhotoName())) {
                boolean deleted = FileUtil.deleteFile(appProperties.getProductImageFolder(), p.getPicture());
                if (deleted) {
                    LOGGER.info("ProductService.delete :: deleted file {}", p.getPicture());
                }
            }
            eventPublisher.publishEvent(new ProductEvent(p.getName(), EntityEventType.DELETED));
        });
    }

    public void forceDelete(Product product) {
        productRepository.delete(product);
    }
}
