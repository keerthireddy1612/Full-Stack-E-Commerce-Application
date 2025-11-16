package com.ecommerce.sbecom.controller;

import com.ecommerce.sbecom.config.AppConstants;
import com.ecommerce.sbecom.payload.*;
import com.ecommerce.sbecom.security.services.UserDetailsImpl;
import com.ecommerce.sbecom.service.OrderService;
import com.ecommerce.sbecom.service.StripeService;
import com.ecommerce.sbecom.util.AuthUtil;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private StripeService stripeService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod, @RequestBody OrderRequestDTO orderRequestDTO){
        String emailId= authUtil.loggedInEmail();
       OrderDTO orderDTO= orderService.placeOrder(
                emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage()
        );
       return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }
    @PostMapping("/order/stripe-client-secret")
    public ResponseEntity<String> createStripeClientSecret( @RequestBody StripePaymentDTO stripePaymentDTO) throws StripeException {
        PaymentIntent paymentIntent = stripeService.paymentIntent(stripePaymentDTO);
        return new ResponseEntity<>(paymentIntent.getClientSecret(), HttpStatus.CREATED);
    }

    @GetMapping("/admin/orders")
    public ResponseEntity<OrderResponse> getAllOrders(
            @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
            @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name="sortBy", defaultValue = AppConstants.SORT_ORDERS_BY,required = false) String sortBy,
            @RequestParam(name="sortOrder", defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder
    ){
        OrderResponse orderresponse = orderService.getAllOrders(pageNumber,pageSize,pageSize, sortBy, sortOrder);
        return new ResponseEntity<OrderResponse>(orderresponse, HttpStatus.OK);
    }

    @PutMapping("/admin/orders/{orderId}/status")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderStatusUpdateDTO orderStatusupdateDTO){
        OrderDTO order= orderService.updateOrder(orderId,orderStatusupdateDTO.getStatus());
        return new ResponseEntity<>(order, HttpStatus.OK);

    }

}
