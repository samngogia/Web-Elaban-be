package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.ProductImageRespository;
import NgoGiaSam.Web_Elaban_be.dao.ProductRespository;
import NgoGiaSam.Web_Elaban_be.enity.Product;
import NgoGiaSam.Web_Elaban_be.enity.ProductImage;
import NgoGiaSam.Web_Elaban_be.service.FileStorageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageController {
    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ProductImageRespository productImageRespository;

    @Autowired
    private ProductRespository productRespository;

    @PostMapping("/upload/{productId}")
    public ResponseEntity<String> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isThumbnail", defaultValue = "false") boolean isThumbnail) {

        try {
            Product product = productRespository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            String url = fileStorageService.saveFile(file);

            ProductImage image = new ProductImage();
            image.setProduct(product);
            image.setName(file.getOriginalFilename());
            image.setUrl(url);
            image.setThumbnail(isThumbnail);
            image.setData(null); // không dùng base64 nữa

            productImageRespository.save(image);

            return ResponseEntity.ok(url);

        } catch (IOException e) {
            // Trả về lỗi 500 nếu quá trình lưu file vật lý thất bại
            return ResponseEntity.status(500).body("Could not save file: " + e.getMessage());
        }
    }
}
