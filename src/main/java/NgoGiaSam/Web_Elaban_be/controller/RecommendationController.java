package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.ProductRecommendationRepository;
import NgoGiaSam.Web_Elaban_be.dao.ProductRespository;
import NgoGiaSam.Web_Elaban_be.enity.ProductRecommendation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {
    private final ProductRecommendationRepository recommendationRepo;
    private final ProductRespository productRespository;

    // Dải 1 — Content-Based: sản phẩm tương tự
    @GetMapping("/similar/{productId}")
    @Transactional
    public ResponseEntity<?> getSimilar(@PathVariable Long productId) {
        List<ProductRecommendation> recs = recommendationRepo
                .findContentBasedByProductId(productId, PageRequest.of(0, 8));
        return ResponseEntity.ok(mapToProducts(recs));
    }

    // Dải 2 — Apriori: thường mua cùng
    @GetMapping("/bought-together/{productId}")
    @Transactional
    public ResponseEntity<?> getBoughtTogether(@PathVariable Long productId) {
        List<ProductRecommendation> recs = recommendationRepo
                .findAprioriByProductId(productId, PageRequest.of(0, 8));
        return ResponseEntity.ok(mapToProducts(recs));
    }

    private List<Map<String, Object>> mapToProducts(
            List<ProductRecommendation> recs) {
        return recs.stream()
                .map(r -> productRespository.findById(r.getRecommendedId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id",           p.getId());
                    map.put("name",         p.getName());
                    map.put("sellingPrice", p.getSellingPrice());
                    map.put("listPrice",    p.getListPrice());
                    map.put("avgRating",    p.getAvgRating());
                    map.put("brand",        p.getBrand());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
