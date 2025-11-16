package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.exceptions.APIException;
import com.ecommerce.sbecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecom.model.Cart;
import com.ecommerce.sbecom.model.CartItem;
import com.ecommerce.sbecom.model.Product;
import com.ecommerce.sbecom.payload.CartDTO;
import com.ecommerce.sbecom.payload.CartItemDTO;
import com.ecommerce.sbecom.payload.ProductDTO;
import com.ecommerce.sbecom.repository.CartItemRepository;
import com.ecommerce.sbecom.repository.CartRepository;
import com.ecommerce.sbecom.repository.ProductRepository;
import com.ecommerce.sbecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {
        //Find existing cart or create new cart
        Cart cart= createCart();

        //Find product by productId If product not found, throw exception
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        //perform validations
       CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),productId);
       if(cartItem!=null){
           throw new APIException(product.getProductName() + " already exists in cart. Please update quantity from cart");
       }
       if(product.getQuantity()==0){
           throw new APIException(product.getProductName() + "is not available");
       }
        if(product.getQuantity()<quantity){
            throw new APIException("Please , make an order of the" + product.getProductName()+ " less than or equal to " + product.getQuantity());
        }
        //Create a cart item and add to cart
        CartItem newCartItem=new CartItem();

        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setProductPrice(product.getSpecialPrice());
        newCartItem.setDiscount(product.getDiscount());

        cart.getCartItems().add(newCartItem);

        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());//you can reduce the product quantity here if needed -quantity to the expression. but we reduce stock when order is placed
        cart.setTotalPrice(cart.getTotalPrice()+(product.getSpecialPrice()*quantity));
        cartRepository.save(cart);
        //convert cart to cartDTO

        CartDTO  cartDTO= modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems= cart.getCartItems();
        Stream<ProductDTO> productDTOStream= cartItems.stream().map(item -> {
            ProductDTO map= modelMapper.map(item.getProduct(), ProductDTO.class);
          map.setQuantity(item.getQuantity());
        return map;
        });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts= cartRepository.findAll();
        if(carts.size()==0){
            throw new APIException("No carts found");
        }
        List<CartDTO> cartDTOS= carts.stream().map(cart -> {
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            List<ProductDTO> products= cart.getCartItems().stream()
                    .map(item->{
                        ProductDTO dto= modelMapper.map(item.getProduct(), ProductDTO.class);
                        dto.setQuantity(item.getQuantity());
                        return dto;
                    }).collect(Collectors.toList());

            cartDTO.setProducts(products);
            return cartDTO;
        }).collect(Collectors.toList());
        return cartDTOS;
    }

    @Override
    public CartDTO getCart(String emailId, Long cartId) {
        Cart cart= cartRepository.findCartByEmailAndCartId(emailId,cartId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart","cartId",cartId);}
        CartDTO cartDTO= modelMapper.map(cart,CartDTO.class);
        cart.getCartItems().forEach(c -> {c.getProduct().setQuantity(c.getQuantity());});
        List<ProductDTO> products= cart.getCartItems().stream()
                .map(p->modelMapper.map(p.getProduct(),ProductDTO.class))
                .collect(Collectors.toList());
        cartDTO.setProducts(products);
        return cartDTO;
    }
    @Override
    @Transactional
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {

        Cart userCart= cartRepository.findCartByEmail((authUtil.loggedInEmail()));
        Long cartId=userCart.getCartId();
        Cart cart= cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));
        Product product= productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));
        if(product.getQuantity()==0){
            throw new APIException(product.getProductName() + "is not available");
        }
        if(product.getQuantity()<quantity){
            throw new APIException("Please , make an order of the" + product.getProductName()+ " less than or equal to " + product.getQuantity());
        }

        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("product" + product.getProductName() + "is not available");}

        int newQuantity = cartItem.getQuantity()+quantity;
        if(newQuantity <0){
            throw new APIException("The resulting quantity cannot be negative");
        }
        if(newQuantity==0){
            deleteProductFromCart(cartId, productId);
        }else{
        cartItem.setProductPrice(product.getSpecialPrice());
        cartItem.setQuantity(cartItem.getQuantity()+quantity);
        cartItem.setDiscount(product.getDiscount());
        cart.setTotalPrice(cart.getTotalPrice()+(cartItem.getProductPrice()*quantity));
        cartRepository.save(cart);}

        CartItem updatedItem= cartItemRepository.save(cartItem);
        if(updatedItem.getQuantity()==0){
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }
        CartDTO cartDTO= modelMapper.map(cart,CartDTO.class);
        List<CartItem> cartItems= cart.getCartItems();
        Stream<ProductDTO> productDTO= cartItems.stream().map(item -> {
            ProductDTO prd= modelMapper.map(item.getProduct(), ProductDTO.class);
            prd.setQuantity(item.getQuantity());
            return prd;
        });
        cartDTO.setProducts(productDTO.toList());
        return cartDTO;
    }
    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart= cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));
        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new ResourceNotFoundException("Product" , "productId", productId);
        }
        cart.setTotalPrice(cart.getTotalPrice()-(cartItem.getProductPrice()*cartItem.getQuantity()));
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        return "Product" + cartItem.getProduct().getProductName()+ " removed from the cart";
    }
    @Override
    public void updateProductInCarts(Long cartId, Long productId) {
        Cart cart= cartRepository.findById(cartId).orElseThrow(()-> new ResourceNotFoundException("Cart","cartId",cartId));
        Product product= productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product","productId",productId));

        CartItem cartItem= cartItemRepository.findCartItemByProductIdAndCartId(cartId,productId);
        if(cartItem==null){
            throw new APIException("product" + product.getProductName() + "is not available in the cart");
        }
        double cartPrice= cart.getTotalPrice()- (cartItem.getProductPrice()*cartItem.getQuantity());
        cartItem.setProductPrice(product.getSpecialPrice());
        cart.setTotalPrice(cartPrice+(cartItem.getProductPrice()*cartItem.getQuantity()));

        cartItem=cartItemRepository.save(cartItem);

    }

    private Cart createCart(){
        Cart userCart= cartRepository.findCartByEmail((authUtil.loggedInEmail()));
        if(userCart!=null){
            return userCart;
        }
        Cart cart=new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        Cart newCart=cartRepository.save(cart);
        return newCart;
    }

    @Override
    @Transactional
    public String createOrUpdateCartWithItems(List<CartItemDTO> cartItems) {
        String emailId = authUtil.loggedInEmail();
        Cart existingCart = cartRepository.findCartByEmail(emailId);
        if(existingCart==null){
            existingCart=createCart();
            existingCart.setTotalPrice(0.00);
            existingCart.setUser(authUtil.loggedInUser());
            existingCart=cartRepository.save(existingCart);
        } else{
            //clear all current items
           cartItemRepository.deleteAllByCartId(existingCart.getCartId());
        }
        double totalPrice=0.00;

        for(CartItemDTO cartItemDTO:cartItems){
            Long productId = cartItemDTO.getProductId();
            Integer quantity = cartItemDTO.getQuantity();

            Product product = productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException("Product", "productId", productId));
            //product.setQuantity(product.getQuantity()-quantity);
            totalPrice +=product.getSpecialPrice() * quantity;

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(existingCart);
            cartItem.setQuantity(quantity);
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setDiscount(product.getDiscount());
            cartItemRepository.save(cartItem);
        }
        existingCart.setTotalPrice(totalPrice);
        cartRepository.save(existingCart);
        return "Cart created/updated with new items successfully";
    }

}
