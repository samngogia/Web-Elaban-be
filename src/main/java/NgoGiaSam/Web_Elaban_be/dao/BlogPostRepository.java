package NgoGiaSam.Web_Elaban_be.dao;

import NgoGiaSam.Web_Elaban_be.enity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    List<BlogPost> findByIsPublishedTrueOrderByCreatedDateDesc();
    List<BlogPost> findByCategory_IdAndIsPublishedTrueOrderByCreatedDateDesc(Long categoryId);
    Optional<BlogPost> findBySlug(String slug);
    List<BlogPost> findAllByOrderByCreatedDateDesc();
}