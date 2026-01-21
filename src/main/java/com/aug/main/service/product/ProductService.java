package com.aug.main.service.product;

import com.aug.main.dto.ImageDto;
import com.aug.main.dto.ProductDto;
import com.aug.main.exceptions.AlreadyExistsException;
import com.aug.main.exceptions.ResourceNotFoundException;
import com.aug.main.model.Category;
import com.aug.main.model.Image;
import com.aug.main.model.Product;
import com.aug.main.repository.CategoryRepository;
import com.aug.main.repository.ImageRepository;
import com.aug.main.repository.ProductRepository;
import com.aug.main.request.AddProductRequest;
import com.aug.main.request.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ImageRepository imageRepository;
    private final ModelMapper modelMapper;

    @Override
    public Product addProduct(AddProductRequest request) {
        if(productExists(request.getName(), request.getBrand() )) {
            throw new AlreadyExistsException(request.getBrand() + " " + request.getName()+ " already exists, you may update this product instead!   ");
        }
/*
//        Category category = Optional.ofNullable(categoryRepository.findByName(request.getCategory().getName()))
//                .orElseGet(() -> {
//                    Category newCategory = new Category(request.getCategory().getName());
//                    return categoryRepository.save(newCategory);
//                });
 */
        Category category;
        Category reqCategory = request.getCategory();
        if(reqCategory.getId() != null) {
            category = categoryRepository.findById(reqCategory.getId()).orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + reqCategory.getId()));
        } else if (reqCategory.getName() != null && !reqCategory.getName().isBlank()) {
            category = categoryRepository.findByName(reqCategory.getName()).orElseGet(() -> categoryRepository.save(new Category(reqCategory.getName())));
        } else {
            throw new IllegalArgumentException("Category id or name must be provided.");
        }
        request.setCategory(category);
        return productRepository.save(createProduct(request, category));
    }

    private boolean productExists(String name, String brand) {
        return productRepository.existsByNameAndBrand(name, brand);
    }

    private Product createProduct(AddProductRequest request, Category category) {
        return new Product(
                request.getName(),
                request.getBrand(),
                request.getPrice(),
                request.getInventory(),
                request.getDescription(),
                category
        );
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository
                .findById(id)
                .ifPresentOrElse(
                        productRepository::delete,
                        () -> {
                            throw new ResourceNotFoundException("Product not found");
                        });
    }

    @Override
    public Product updateProduct(ProductUpdateRequest request, Long productId) {

        return productRepository.findById(productId)
                .map(existingProduct -> updateExistingProduct(existingProduct, request))
                .map(productRepository::save)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request) {
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setDescription(request.getDescription());
//        Category category = categoryRepository.findByName(request.getCategory().getName());
        Category category;
        Category reqCategory = request.getCategory();
        if(reqCategory != null) {
            if(reqCategory.getId() != null) {
                category = categoryRepository.findById(reqCategory.getId()).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            } else if(reqCategory.getName() != null && !reqCategory.getName().isBlank()) {
                category = categoryRepository.findByName(reqCategory.getName()).orElseGet(() -> categoryRepository.save(new Category(reqCategory.getName())));
            } else {
                throw new IllegalArgumentException("Category id or name must be provided.");
            }
            existingProduct.setCategory(category);
        }
        return existingProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryName(category);
    }

    @Override
    public List<Product> getProductsByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductsByCategoryAndBrand(String category, String brand) {
        return productRepository.findByCategoryNameAndBrand(category, brand);
    }

    @Override
    public List<Product> getProductsByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductsByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand, name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand, name);
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products) {
        return products.stream().map(this::convertToDto).toList();
    }

    @Override
    public ProductDto convertToDto(Product product) {
        ProductDto productDto = modelMapper.map(product, ProductDto.class);
        List<Image> images = imageRepository.findByProductId(product.getId());
        List<ImageDto> imageDtos = images.stream().map((image) -> modelMapper.map(image, ImageDto.class)).toList();
        productDto.setImages(imageDtos);
        return productDto;
    }
}
