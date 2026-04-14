package NgoGiaSam.Web_Elaban_be.enity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
@Table(name = "product_recommendation")
public class ProductRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "recommended_id")
    private Long recommendedId;

    private double support;
    private double confidence;
    private double lift;
    private String algorithm;
}
