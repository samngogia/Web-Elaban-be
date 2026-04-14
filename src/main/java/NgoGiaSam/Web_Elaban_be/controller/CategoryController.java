package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.CategoryRespository;
import NgoGiaSam.Web_Elaban_be.enity.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryRespository categoryRespository;

    @GetMapping
    @Transactional
    public ResponseEntity<?> getAllCategories() {
        List<Category> categories = categoryRespository.findAll();

        List<Map<String, Object>> result = categories.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("name", c.getName());
            map.put("parentId", c.getParent() != null ? c.getParent().getId() : null);
            // KHÔNG map children và products — tránh lazy load
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> createCategory(@RequestBody Map<String, Object> body) {
        Category category = new Category();
        category.setName((String) body.get("name"));

        if (body.get("parentId") != null) {
            Integer parentId = Integer.valueOf(body.get("parentId").toString());
            categoryRespository.findById(parentId).ifPresent(category::setParent);
        }

        return ResponseEntity.ok(categoryRespository.save(category));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateCategory(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> body) {

        return categoryRespository.findById(id).map(c -> {
            c.setName((String) body.get("name"));

            if (body.get("parentId") != null) {
                Integer parentId = Integer.valueOf(body.get("parentId").toString());
                categoryRespository.findById(parentId).ifPresent(c::setParent);
            } else {
                c.setParent(null);
            }

            return ResponseEntity.ok(categoryRespository.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        categoryRespository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

}
