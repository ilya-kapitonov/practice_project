package com.equipment.tracker.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.equipment.tracker.entity.Category;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findAll(Sort sort);
    Category findByCategoryName(String categoryName);
    boolean existsByCategoryName(String categoryName);
}