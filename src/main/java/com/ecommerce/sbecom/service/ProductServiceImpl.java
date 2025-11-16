package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.exceptions.APIException;
import com.ecommerce.sbecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecom.model.Cart;
import com.ecommerce.sbecom.model.Category;
import com.ecommerce.sbecom.model.Product;
import com.ecommerce.sbecom.payload.CartDTO;
import com.ecommerce.sbecom.payload.ProductDTO;
import com.ecommerce.sbecom.payload.ProductResponse;
import com.ecommerce.sbecom.repository.CartRepository;
import com.ecommerce.sbecom.repository.CategoryRepository;
import com.ecommerce.sbecom.repository.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileService fileService;

    @Value("${project.image}")
    private String path;

    @Value("${image.base.url}")
    private String imageBaseUrl;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartService cartService;

    @Override
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        boolean isProductNotPresent=true;
        List<Product> products= category.getProducts();
        for(int i=0;i<products.size();i++){
            if(products.get(i).getProductName().equals(productDTO.getProductName())){
                isProductNotPresent=false;
                break;
            }
        }
       if(isProductNotPresent){
        Product product= modelMapper.map(productDTO, Product.class);
        product.setImage("default.png");
        product.setCategory(category);
        Double specialPrice = product.getPrice() - (product.getPrice() * product.getDiscount() / 100);
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductDTO.class);}
       else{
           throw new APIException("product with the name" + productDTO.getProductName() + " already exists in this category");
       }
    }
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, String keyword, String category) {
        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Specification<Product> spec = (root, query, cb) -> cb.conjunction();

        if (keyword != null && !keyword.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(
                            cb.lower(root.get("productName")),
                            "%" + keyword.toLowerCase() + "%"));
        }
        if (category != null && !category.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("categoryName"), category));
        }
        Page<Product> productPage = productRepository.findAll(spec,pageDetails);

        List<Product> products=productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No product created till now");
        }
        List<ProductDTO> productDTOS = products.stream().map(product ->
        {ProductDTO productDTO= modelMapper.map(product, ProductDTO.class);
            productDTO.setImage(constructImageURL(product.getImage()));
            return productDTO;
        }).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());//pagination details
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    private String constructImageURL(String imageName) {
        return imageBaseUrl.endsWith("/") ? imageBaseUrl +imageName : imageBaseUrl+ "/" +imageName; // Assuming images are served from /images/ endpoint
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category", "CategoryId", categoryId));

        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findByCategory(category, pageDetails);
        List<Product> products=productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No product created till now in this category");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());//pagination details
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword,Integer pageNumber, Integer pageSize,String sortBy, String sortOrder) {
        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);

        Page<Product> productPage = (Page<Product>)productRepository.findByProductNameLikeIgnoreCase("%" + keyword +"%",pageDetails);//for pattern matching
        List<Product> products=productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No product found with this keyword");
        }
        List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());//pagination details
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;

    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO, Long productId) {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));
        Product product = modelMapper.map(productDTO, Product.class);

        existingProduct.setProductName(product.getProductName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDiscount(product.getDiscount());
        Double specialPrice = product.getPrice() - (product.getPrice() * product.getDiscount() / 100);
        existingProduct.setSpecialPrice(specialPrice);
        Product updatedProduct = productRepository.save(existingProduct);

        List<Cart> carts= cartRepository.findCartsByProductId(productId);//loop over all carts to update product details
        List<CartDTO> cartDTOs = carts.stream().map(cart ->{
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products = cart.getCartItems().stream().map(p -> modelMapper.map(p.getProduct(), ProductDTO.class))
                    .collect(toList());
            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(toList());

       cartDTOs.forEach(cart ->{
           cartService.updateProductInCarts(cart.getCartId(),productId);
       });

        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product existingProduct = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));

        List<Cart> carts= cartRepository.findCartsByProductId(productId);//loop over all carts to update product details
        carts.forEach(cart ->{cartService.deleteProductFromCart(cart.getCartId(),productId);});

        productRepository.delete(existingProduct);
        return modelMapper.map(existingProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product productFromDB= productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product", "ProductId", productId));
        String fileName= fileService.uploadImage(path, image);//go to filename and upload image
        productFromDB.setImage(fileName);
        Product updatedProduct= productRepository.save(productFromDB);
        return modelMapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public ProductResponse getAllProductsForAdmin(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder= sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();

        Pageable pageDetails= PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Product> productPage = productRepository.findAll(pageDetails);

        List<Product> products=productPage.getContent();
        if(products.isEmpty()){
            throw new APIException("No product created till now");
        }
        List<ProductDTO> productDTOS = products.stream().map(product ->
        {ProductDTO productDTO= modelMapper.map(product, ProductDTO.class);
            productDTO.setImage(constructImageURL(product.getImage()));
            return productDTO;
        }).toList();

        ProductResponse productResponse=new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPageNumber(productPage.getNumber());//pagination details
        productResponse.setPageSize(productPage.getSize());
        productResponse.setTotalElements(productPage.getTotalElements());
        productResponse.setTotalPages(productPage.getTotalPages());
        productResponse.setLastPage(productPage.isLast());
        return productResponse;
    }

}
