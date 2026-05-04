package NgoGiaSam.Web_Elaban_be.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductSalesDTO {
    private Long id;
    private String name;
    private Long totalSold;
    private Integer quantity;

    // Phiên bản 1: Dành cho trường hợp Product ID là Long, Quantity là Integer (chuẩn nhất)
    public ProductSalesDTO(Long id, String name, Long totalSold, Integer quantity) {
        this.id = id;
        this.name = name;
        this.totalSold = totalSold;
        this.quantity = quantity;
    }

    // Phiên bản 2: Dành cho trường hợp Product ID là Integer (rất hay gặp nếu copy code mẫu)
    public ProductSalesDTO(Integer id, String name, Long totalSold, Integer quantity) {
        this.id = (id != null) ? id.longValue() : null;
        this.name = name;
        this.totalSold = totalSold;
        this.quantity = quantity;
    }

    // Phiên bản 3: Phòng hờ Hibernate hứng chí ép cái quantity tồn kho thành Long luôn
    public ProductSalesDTO(Long id, String name, Long totalSold, Long quantity) {
        this.id = id;
        this.name = name;
        this.totalSold = totalSold;
        this.quantity = (quantity != null) ? quantity.intValue() : 0;
    }

    // Phiên bản 4: ID là Integer và Quantity cũng là Long
    public ProductSalesDTO(Integer id, String name, Long totalSold, Long quantity) {
        this.id = (id != null) ? id.longValue() : null;
        this.name = name;
        this.totalSold = totalSold;
        this.quantity = (quantity != null) ? quantity.intValue() : 0;
    }
}