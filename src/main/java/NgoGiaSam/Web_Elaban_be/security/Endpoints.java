package NgoGiaSam.Web_Elaban_be.security;

public class Endpoints {
    public static final String FRONT_END_HOST = "http://localhost:3000";

    public static final String[] PUBLIC_GET_ENDPOINTS = {
            "/products",// sản phẩm
            "/products/**",
            "/images",//hình ảnh
            "/images/**",
            "/product_image",       // thêm dòng này
            "/product_image/**",// thêm dòng này
            "/uploads/**",
            "/reviews",          // thêm dòng này
            "/reviews/**",
            "/reviews/search/**",
//            "/users/search/existsByUsername",
//            "/users/search/existsByEmail",
            "/users/search/**",//nguoidung/search
            "/account/activate" ,// tai khoản
            "/cart/**",
            "cart/{userId}",
            "/payment_method",
            "/payment_method/**",
            "/shipping_method",
            "/shipping_method/**",
            "/reviews/search/**",

            "/admin/orders/my-orders/**",

            "/api/wishlist/**",


            "/api/recommendations/**",

            "/api/recommendations/**",   // thêm nếu chưa có
            "/reviews/search/**",        // thêm nếu chưa có
            "/order/my-orders/**",
            "/vnpay/payment-return",


    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/account/register",//tài khoản/đăng ký
            "/account/login", //tài khoản//đăng nhập
            "/cart/add",
            "/order/checkout",


            "/account/forgot-password",  // thêm
            "/account/reset-password",   // thêm

            "/vnpay/create-payment",

            "/api/chat",

            "/api/reviews",
            "/api/wishlist/add",
    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/users",//người dùng
            "/users/**",
            "/admin/dashboard/**",  // thêm dòng này

            "/admin/orders",      // thêm
            "/admin/orders/**",   // thêm
            "/admin/categories",   // thêm
            "/admin/categories/**",// thêm
            "/admin/users",        // thêm
            "/admin/users/**",// thêm

    };

    public static final String[] ADMIN_POST_ENDPOINTS = {
            "/products",//sản phẩm
            "/product",
            "/products/**"
    };

}
