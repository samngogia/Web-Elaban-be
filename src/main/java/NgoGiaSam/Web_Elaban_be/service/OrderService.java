package NgoGiaSam.Web_Elaban_be.service;

import NgoGiaSam.Web_Elaban_be.dao.*;
import NgoGiaSam.Web_Elaban_be.dto.CheckoutRequest;
import NgoGiaSam.Web_Elaban_be.dto.OrderResponse;
import NgoGiaSam.Web_Elaban_be.enity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRespository orderRespository;
    private final OrderDetailRespository orderDetailRespository;
    private final UserRespository userRespository;
    private final PaymentMethodRespository paymentMethodRespository;
    private final ShippingMethodRespository shippingMethodRespository;
    private final ProductRespository productRespository;
    private final CartRespository cartRespository;
    private final CartItemRespository cartItemRespository;

    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {

        // 1. Lấy thông tin cần thiết
        User user = userRespository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        PaymentMethod paymentMethod = paymentMethodRespository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new RuntimeException("Payment method not found"));

        ShippingMethod shippingMethod = shippingMethodRespository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new RuntimeException("Shipping method not found"));

        // 2. Tính tổng tiền hàng
        double totalPriceProduct = request.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        double totalAmount = totalPriceProduct + shippingMethod.getShippingFee()
                + paymentMethod.getPaymentFee();

        // 3. Tạo Order
        Order order = new Order();
        order.setUser(user);
        order.setCreatedDate(new Date());
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setPaymentMethod(paymentMethod);
        order.setShippingMethod(shippingMethod);
        order.setTotalPriceProduct(totalPriceProduct);
        order.setShippingFee(shippingMethod.getShippingFee());
        order.setTotalAmount(totalAmount);
        order.setPaymentStatus("UNPAID");
        order.setShippingStatus("PENDING");
        order.setFullName(request.getFullName());
        order.setPhoneNumber(request.getPhoneNumber());
        order.setNote(request.getNote());


        Order savedOrder = orderRespository.save(order);

        // 4. Tạo OrderDetail cho từng sản phẩm
        for (CheckoutRequest.CheckoutItem item : request.getItems()) {
            Product product = productRespository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            orderDetailRespository.save(detail);
        }

        // 5. Xóa giỏ hàng sau khi đặt hàng
        cartRespository.findByUser_Id(request.getUserId()).ifPresent(cart -> {
            cartItemRespository.deleteAll(
                    cartItemRespository.findByCart_Id(cart.getId())
            );
        });

        return new OrderResponse(
                savedOrder.getId(),
                totalAmount,
                "UNPAID",
                "PENDING"
        );
    }
}
