package NgoGiaSam.Web_Elaban_be.config;

import NgoGiaSam.Web_Elaban_be.enity.Category;
import NgoGiaSam.Web_Elaban_be.enity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MethodRestConfig implements RepositoryRestConfigurer {
    @Autowired
    private EntityManager entityManager;

    @Override
    public void configureRepositoryRestConfiguration(
            RepositoryRestConfiguration config,
            CorsRegistry cors) {

        // Expose ID cho TẤT CẢ entity
        config.exposeIdsFor(
                entityManager.getMetamodel()
                        .getEntities()
                        .stream()
                        .map(Type::getJavaType)
                        .toArray(Class[]::new)
        );

        //CORS configuration
        cors.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE");

        // Các HTTP method cần chặn
        HttpMethod[] chanTatCa = {
                HttpMethod.POST,
                HttpMethod.PUT,
                HttpMethod.DELETE
        };

        // Chặn CRUD cho TheLoai
        disableHttpMethods(Category.class, config, chanTatCa);

        // Chặn DELETE cho NguoiDung
        HttpMethod[] chanDelete = {
                HttpMethod.DELETE
        };
        disableHttpMethods(User.class, config, chanDelete);
    }

    private void disableHttpMethods(
            Class<?> domainType,
            RepositoryRestConfiguration config,
            HttpMethod[] methods) {

        config.getExposureConfiguration()
                .forDomainType(domainType)
                .withItemExposure((metadata, httpMethods) ->
                        httpMethods.disable(methods))
                .withCollectionExposure((metadata, httpMethods) ->
                        httpMethods.disable(methods));
    }
}
