package kr.moonwalk.moonwalk_api.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectModuleRepositoryCustomImpl implements ProjectModuleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> findCategoryPriceDetailsByProjectId(Long projectId) {
        String jpql = "SELECT c.name AS categoryName, " +
            "       m.name AS moduleName, " +
            "       m.price AS modulePrice, " +
            "       pm.id AS projectModuleId " +
            "FROM ProjectModule pm " +
            "JOIN pm.module m " +
            "JOIN m.category c " +
            "WHERE pm.project.id = :projectId";

        Query query = entityManager.createQuery(jpql);
        query.setParameter("projectId", projectId);

        return query.getResultList();
    }



}
