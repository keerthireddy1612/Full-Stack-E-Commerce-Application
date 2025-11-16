package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.payload.OrderDTO;
import com.ecommerce.sbecom.payload.OrderResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public interface OrderService {
    OrderDTO placeOrder(String emailId, Long addressId, String paymentMethod, String pgName, String pgPaymentId, String pgStatus, String pgResponseMessage);

    OrderResponse getAllOrders(Integer pageNumber, Integer pageSize, Integer pageSize1, String sortBy, String sortOrder);

    OrderDTO updateOrder(Long orderId, String status);
}
