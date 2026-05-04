package NgoGiaSam.Web_Elaban_be.service;

import NgoGiaSam.Web_Elaban_be.dao.*;
import NgoGiaSam.Web_Elaban_be.dto.CheckoutRequest;
import NgoGiaSam.Web_Elaban_be.dto.OrderResponse;
import NgoGiaSam.Web_Elaban_be.enity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final EmailService emailService;

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

        // 4. Tạo OrderDetail và TRỪ SỐ LƯỢNG TỒN KHO
        for (CheckoutRequest.CheckoutItem item : request.getItems()) {
            Product product = productRespository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // --- ĐOẠN MỚI THÊM: Kiểm tra và trừ số lượng sản phẩm ---
            Long currentQuantity = product.getQuantity();
            Long buyQuantity = item.getQuantity();

            if (currentQuantity < buyQuantity) {
                throw new RuntimeException("Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho (Chỉ còn " + currentQuantity + ").");
            }

            // Trừ kho và lưu lại

            product.setQuantity(currentQuantity - buyQuantity);
            productRespository.save(product);
            // --------------------------------------------------------

            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setQuantity( buyQuantity);
            detail.setPrice(item.getPrice());
            orderDetailRespository.save(detail);
        }

        // 5. Dọn dẹp giỏ hàng (Chỉ xóa những món đã đặt thành công)
        cartRespository.findByUser_Id(request.getUserId()).ifPresent(cart -> {
            List<CartItem> currentCartItems = cartItemRespository.findByCart_Id(cart.getId());

            for (CheckoutRequest.CheckoutItem checkoutItem : request.getItems()) {
                // Tìm trong giỏ hàng xem có món này không, nếu có thì xóa
                currentCartItems.stream()
                        .filter(cItem -> cItem.getProduct().getId().equals(checkoutItem.getProductId()))
                        .findFirst()
                        .ifPresent(cartItemRespository::delete);
            }
        });

        // 6. Gửi Email thông báo
        try {
            String customerEmail = user.getEmail();
            if (customerEmail != null && !customerEmail.isEmpty()) {
                System.out.println("Đang chuẩn bị gửi mail cho: " + customerEmail);
                emailService.sendOrderConfirmationEmail(customerEmail, savedOrder);
                System.out.println(">>> ĐÃ GỬI MAIL THÀNH CÔNG! <<<");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("LỖI KHI GỬI EMAIL: " + e.getMessage());
        }

        return new OrderResponse(
                savedOrder.getId(),
                totalAmount,
                "UNPAID",
                "PENDING"
        );
    }

    @Transactional
    public List<Map<String, Object>> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRespository.findByUser_IdOrderByCreatedDateDesc(userId);
        return orders.stream().map(o -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", o.getId());
            map.put("totalAmount", o.getTotalAmount());
            map.put("paymentStatus", o.getPaymentStatus());
            map.put("shippingStatus", o.getShippingStatus());
            map.put("shippingAddress", o.getShippingAddress());
            map.put("fullName", o.getFullName());
            map.put("phoneNumber", o.getPhoneNumber());
            map.put("createdDate", o.getCreatedDate());
            map.put("paymentMethod", o.getPaymentMethod() != null ? o.getPaymentMethod().getName() : "");
            map.put("orderDetails", o.getOrderDetails().stream().map(d -> {
                Map<String, Object> detail = new HashMap<>();
                detail.put("productId", d.getProduct().getId());
                detail.put("productName", d.getProduct().getName());
                detail.put("quantity", d.getQuantity());
                detail.put("price", d.getPrice());
                return detail;
            }).collect(java.util.stream.Collectors.toList()));
            return map;
        }).collect(java.util.stream.Collectors.toList());
    }
}