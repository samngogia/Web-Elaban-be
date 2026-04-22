package NgoGiaSam.Web_Elaban_be.controller;

import NgoGiaSam.Web_Elaban_be.dao.BlogCategoryRepository;
import NgoGiaSam.Web_Elaban_be.dao.BlogCommentRepository;
import NgoGiaSam.Web_Elaban_be.dao.BlogPostRepository;
import NgoGiaSam.Web_Elaban_be.dao.UserRespository;
import NgoGiaSam.Web_Elaban_be.enity.BlogComment;
import NgoGiaSam.Web_Elaban_be.enity.BlogPost;
import NgoGiaSam.Web_Elaban_be.enity.User;
import NgoGiaSam.Web_Elaban_be.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogPostRepository blogPostRepository;
    private final BlogCategoryRepository blogCategoryRepository;
    private final BlogCommentRepository blogCommentRepository;
    private final UserRespository userRespository;
    private final JwtService jwtService;
    // ── PUBLIC ──────────────────────────────────────────

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories() {
        return ResponseEntity.ok(blogCategoryRepository.findAll());
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getPublishedPosts(
            @RequestParam(required = false) Long categoryId) {
        List<BlogPost> posts = categoryId != null
                ? blogPostRepository.findByCategory_IdAndIsPublishedTrueOrderByCreatedDateDesc(categoryId)
                : blogPostRepository.findByIsPublishedTrueOrderByCreatedDateDesc();
        return ResponseEntity.ok(posts.stream().map(this::mapPost).toList());
    }

    @GetMapping("/posts/{slug}")
    public ResponseEntity<?> getPostBySlug(@PathVariable String slug) {
        return blogPostRepository.findBySlug(slug).map(post -> {
            post.setViewCount(post.getViewCount() + 1);
            blogPostRepository.save(post);
            return ResponseEntity.ok(mapPostDetail(post));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── ADMIN ────────────────────────────────────────────

    @GetMapping("/admin/posts")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(
                blogPostRepository.findAllByOrderByCreatedDateDesc()
                        .stream().map(this::mapPost).toList()
        );
    }

    @PostMapping("/admin/posts")
    @Transactional
    public ResponseEntity<?> createPost(@RequestBody Map<String, Object> body) {
        BlogPost post = new BlogPost();
        fillPost(post, body);
        return ResponseEntity.ok(mapPost(blogPostRepository.save(post)));
    }

    @PutMapping("/admin/posts/{id}")
    @Transactional
    public ResponseEntity<?> updatePost(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return blogPostRepository.findById(id).map(post -> {
            fillPost(post, body);
            return ResponseEntity.ok(mapPost(blogPostRepository.save(post)));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/admin/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        blogPostRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa!");
    }

    @PatchMapping("/admin/posts/{id}/toggle-publish")
    public ResponseEntity<?> togglePublish(@PathVariable Long id) {
        return blogPostRepository.findById(id).map(post -> {
            post.setPublished(!post.isPublished());
            blogPostRepository.save(post);
            return ResponseEntity.ok(post.isPublished() ? "Đã đăng!" : "Đã ẩn!");
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── Helpers ──────────────────────────────────────────

    private void fillPost(BlogPost post, Map<String, Object> body) {
        post.setTitle((String) body.get("title"));
        post.setSlug(generateSlug((String) body.get("title")));
        post.setSummary((String) body.get("summary"));
        post.setContent((String) body.get("content"));
        post.setThumbnail((String) body.get("thumbnail"));
        post.setAuthor((String) body.getOrDefault("author", "Admin"));
        post.setPublished(Boolean.TRUE.equals(body.get("isPublished")));
        if (body.get("categoryId") != null) {
            Long catId = Long.valueOf(body.get("categoryId").toString());
            blogCategoryRepository.findById(catId).ifPresent(post::setCategory);
        }
    }

    private Map<String, Object> mapPost(BlogPost p) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", p.getId());
        map.put("title", p.getTitle());
        map.put("slug", p.getSlug());
        map.put("summary", p.getSummary());
        map.put("thumbnail", p.getThumbnail());
        map.put("author", p.getAuthor());
        map.put("isPublished", p.isPublished());
        map.put("viewCount", p.getViewCount());
        map.put("createdDate", p.getCreatedDate());
        if (p.getCategory() != null) {
            map.put("categoryId", p.getCategory().getId());
            map.put("categoryName", p.getCategory().getName());
        }
        return map;
    }

    private Map<String, Object> mapPostDetail(BlogPost p) {
        Map<String, Object> map = mapPost(p);
        map.put("content", p.getContent());
        return map;
    }

    private String generateSlug(String title) {
        if (title == null) return "";
        String slug = title.toLowerCase()
                .replaceAll("[àáảãạăắặẵẳâầấậẫẩ]", "a")
                .replaceAll("[èéẻẽẹêềếệễể]", "e")
                .replaceAll("[ìíỉĩị]", "i")
                .replaceAll("[òóỏõọôồốộỗổơờớợỡở]", "o")
                .replaceAll("[ùúủũụưừứựữử]", "u")
                .replaceAll("[ỳýỷỹỵ]", "y")
                .replaceAll("[đ]", "d")
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
        return slug + "-" + System.currentTimeMillis();
    }


    // Lấy bình luận của bài viết
    @GetMapping("/posts/{slug}/comments")
    @Transactional
    public ResponseEntity<?> getComments(@PathVariable String slug) {
        return blogPostRepository.findBySlug(slug).map(post -> {
            List<Map<String, Object>> comments = blogCommentRepository
                    .findByPost_IdOrderByCreatedDateDesc(post.getId())
                    .stream().map(c -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", c.getId());
                        map.put("content", c.getContent());
                        map.put("createdDate", c.getCreatedDate());
                        map.put("username", c.getUser().getUsername());
                        map.put("userId", c.getUser().getId());
                        return map;
                    }).toList();
            return ResponseEntity.ok(comments);
        }).orElse(ResponseEntity.notFound().build());
    }

    // Gửi bình luận
    @PostMapping("/posts/{slug}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable String slug,
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer "))
                return ResponseEntity.status(401).body("Vui lòng đăng nhập!");

            String token    = authHeader.substring(7);
            String username = jwtService.extracUsername(token);
            User user = userRespository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

// Sau đó bạn có thể bỏ qua bước check if (user == null) ở dòng dưới
            if (user == null) return ResponseEntity.status(401).body("User not found");

            String content = body.get("content");
            if (content == null || content.trim().isEmpty())
                return ResponseEntity.badRequest().body("Nội dung không được trống!");

            return blogPostRepository.findBySlug(slug).map(post -> {
                BlogComment comment = new BlogComment();
                comment.setPost(post);
                comment.setUser(user);
                comment.setContent(content.trim());
                blogCommentRepository.save(comment);

                Map<String, Object> result = new HashMap<>();
                result.put("id", comment.getId());
                result.put("content", comment.getContent());
                result.put("username", user.getUsername());
                result.put("createdDate", comment.getCreatedDate());
                return ResponseEntity.ok(result);
            }).orElse(ResponseEntity.notFound().build());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    // Admin xóa bình luận
    @DeleteMapping("/admin/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        blogCommentRepository.deleteById(id);
        return ResponseEntity.ok("Đã xóa!");
    }
}
