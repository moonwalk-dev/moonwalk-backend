package kr.moonwalk.moonwalk_api.repository;

import java.util.List;

public interface ProjectModuleRepositoryCustom {
    List<Object[]> findCategoryPriceDetailsByProjectId(Long projectId);
}