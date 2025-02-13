package kr.moonwalk.moonwalk_api.repository;

import java.util.List;
import java.util.Optional;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByNameAndType(String name, Type type);

    boolean existsByNameAndType(String name, Type type);

    @Query("SELECT c.id FROM Category c WHERE c.name IN :names AND c.type = :type")
    List<Long> findIdsByNameInAndType(@Param("names") List<String> names, @Param("type") Category.Type type);

    @Query("SELECT c.id FROM Category c WHERE c.id = :categoryId OR c.parentCategory.id = :categoryId")
    List<Long> findAllSubCategoryIds(@Param("categoryId") Long categoryId);

    @Query("SELECT c.id FROM Category c WHERE c.type = :type AND (c.id = :categoryId OR c.parentCategory.id = :categoryId)")
    List<Long> findAllSubCategoryIdsById(@Param("categoryId") Long categoryId, @Param("type") Type type);

}
