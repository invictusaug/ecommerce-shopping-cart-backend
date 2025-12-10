package com.aug.main.controller;

import com.aug.main.dto.ProductDto;
import com.aug.main.exceptions.AlreadyExistsException;
import com.aug.main.exceptions.ResourceNotFoundException;
import com.aug.main.model.Product;
import com.aug.main.request.AddProductRequest;
import com.aug.main.request.ProductUpdateRequest;
import com.aug.main.response.ApiResponse;
import com.aug.main.service.product.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService iProductService;

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProducts() {
        List<Product> productList = iProductService.getAllProducts();
        List<ProductDto> productDtoList = iProductService.getConvertedProducts(productList);
        return ResponseEntity.ok(new ApiResponse("Success", productDtoList));
    }

    @GetMapping("/product/{productId}/product")
    public ResponseEntity<ApiResponse> getProductsById(@PathVariable Long productId) {
        try {
            Product product = iProductService.getProductById(productId);
            ProductDto productDto = iProductService.convertToDto(product);
            return ResponseEntity.ok(new ApiResponse("Success", productDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public  ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
        try {
            Product theProduct = iProductService.addProduct(product);
            return ResponseEntity.ok(new ApiResponse("Add Product Success", theProduct));
        } catch (AlreadyExistsException e) {
            return ResponseEntity.status(CONFLICT).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/product/{productId}/update")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest request, @PathVariable Long productId) {
        try {
            Product theProduct = iProductService.updateProduct(request, productId);
            return ResponseEntity.ok(new ApiResponse("Update Product Success", theProduct));
        }  catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/product/{productId}/delete")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long productId) {
        try {
            iProductService.deleteProductById(productId);
            return ResponseEntity.ok(new ApiResponse("Delete Product Success", iProductService.getAllProducts()));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/by/brand-and-name")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@RequestParam String brandName, @RequestParam String productName) {
        try {
            List<Product> products = iProductService.getProductsByBrandAndName(brandName, productName);
            List<ProductDto> productDtoList = iProductService.getConvertedProducts(products);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product Not Found", productName));
            }
            return ResponseEntity.ok(new ApiResponse("Success", productDtoList));
        }  catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/products/by/category-and-brand")
    public ResponseEntity<ApiResponse> getProductByCategoryAndBrand(@RequestParam String category, @RequestParam String brand) {
        try {
            List<Product> products = iProductService.getProductsByCategoryAndBrand(category, brand);
            List<ProductDto> productDtoList = iProductService.getConvertedProducts(products);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product Not Found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Success", productDtoList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("error: ", e.getMessage()));
        }
    }

    @GetMapping("/products/{name}/products")
    public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name) {
        try {
            List<Product> products = iProductService.getProductsByName(name);
            List<ProductDto> productDtoList = iProductService.getConvertedProducts(products);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product Not Found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Success", productDtoList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("error: ", e.getMessage()));
        }
    }

    @GetMapping("/product/by-brand")
    public ResponseEntity<ApiResponse> getProductByBrand(@RequestParam String brand) {
        try {
            List<Product> products = iProductService.getProductsByBrand(brand);
            List<ProductDto> productDtoList = iProductService.getConvertedProducts(products);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product Not Found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Success", productDtoList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/product/{category}/all/products")
    public ResponseEntity<ApiResponse> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = iProductService.getProductsByCategory(category);
            List<ProductDto> productDtoList = iProductService.getConvertedProducts(products);
            if (products.isEmpty()) {
                return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Product Not Found", null));
            }
            return ResponseEntity.ok(new ApiResponse("Success", productDtoList));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("error: ", e.getMessage()));
        }
    }

    @GetMapping("/product/count/by-brand/and-name")
    public  ResponseEntity<ApiResponse> getProductCountByBrandAndName(@RequestParam String brand, @RequestParam String productName) {
        try {
            var count = iProductService.countProductsByBrandAndName(brand, productName);
            return ResponseEntity.ok(new ApiResponse("Product count", count));
        } catch (Exception e) {
            return ResponseEntity.ok(new ApiResponse(e.getMessage(), null));
        }
    }
}
