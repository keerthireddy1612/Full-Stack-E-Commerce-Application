package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.exceptions.APIException;
import com.ecommerce.sbecom.exceptions.ResourceNotFoundException;
import com.ecommerce.sbecom.model.*;
import com.ecommerce.sbecom.payload.OrderDTO;
import com.ecommerce.sbecom.payload.OrderItemDTO;
import com.ecommerce.sbecom.payload.OrderResponse;
import com.ecommerce.sbecom.repository.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage) {
        //Getting userCart
        Cart cart= cartRepository.findCartByEmail(emailId);
        if(cart==null){
            throw new ResourceNotFoundException("Cart", "email", emailId);
        }
        Address address= addressRepository.findById(addressId).orElseThrow(()->new ResourceNotFoundException("Address", "addressId", addressId));

        //Create a new order with payment info
        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Accepted");
        order.setAddress(address);

        Payment payment= new Payment(paymentMethod, pgPaymentId, pgResponseMessage, pgName, pgStatus);
        payment.setOrder(order);
        payment= paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder= orderRepository.save(order);

        //Get items from the cart into order items
        List<CartItem> cartItems = cart.getCartItems();
        if(cartItems==null){
            throw new APIException("Cart is Empty");
        }
        List<OrderItem> orderItems = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            OrderItem orderItem= new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }
        orderItems= orderItemRepository.saveAll(orderItems);

        //Post-Order
        //update product stock
        cart.getCartItems().forEach(item -> {
            int quantity = item.getQuantity();
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity()-quantity);
            productRepository.save(product);

            //clear the cart
            cartService.deleteProductFromCart(cart.getCartId(), item.getProduct().getProductId());
        });
        //send back the order summary
        OrderDTO orderDTO= modelMapper.map(savedOrder, OrderDTO.class);
        orderItems.forEach(orderItem -> {
            orderDTO.getOrderItems().
                    add(modelMapper.map(orderItem, OrderItemDTO.class));
        });
        orderDTO.setAddressId(addressId);
        return orderDTO;
    }

    @Override
    public OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, Integer pageSize1, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Order> pageOrders=orderRepository.findAll(pageDetails);
        List<Order> orders= pageOrders.getContent();
        List<OrderDTO> orderDTOs = orders.stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .toList();
        OrderResponse orderResponse= new OrderResponse();
        orderResponse.setContent(orderDTOs);
        orderResponse.setPageNumber(pageOrders.getNumber());
        orderResponse.setPageSize(pageOrders.getSize());
        orderResponse.setTotalPages(pageOrders.getTotalPages());
        orderResponse.setTotalElements(pageOrders.getTotalElements());
        orderResponse .setLastPage(pageOrders.isLast());
        return orderResponse;

    }

    @Override
    public OrderDTO updateOrder( Long orderId, String status) {
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException("Order", "orderId",orderId));
        order.setOrderStatus(status);
        orderRepository.save(order);
        return modelMapper.map(order, OrderDTO.class);
    }
}
