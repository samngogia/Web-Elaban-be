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
            "/account/activate" // tai khoản
    };

    public static final String[] PUBLIC_POST_ENDPOINTS = {
            "/account/register",//tài khoản/đăng ký
            "/account/login" //tài khoản//đăng nhập
    };

    public static final String[] ADMIN_GET_ENDPOINTS = {
            "/users",//người dùng
            "/users/**",
    };

    public static final String[] ADMIN_POST_ENDPOINTS = {
            "/products",//sản phẩm
    };

}
