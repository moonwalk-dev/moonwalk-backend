package kr.moonwalk.moonwalk_api.repository;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByCategory(Category category);

    List<Module> findByNameContainingIgnoreCase(String query);

    @Query("SELECT m FROM Module m JOIN FETCH m.category c WHERE c.name IN :names AND c.type = :type")
    List<Module> findByCategoryNamesAndType(@Param("names") List<String> names, @Param("type") Category.Type type);

    @Query("SELECT m FROM Module m JOIN FETCH m.category c WHERE c.id IN :categoryIds AND c.type = :type")
    List<Module> findByCategoryIdsAndType(@Param("categoryIds") List<Long> categoryIds, @Param("type") Category.Type type);

}
