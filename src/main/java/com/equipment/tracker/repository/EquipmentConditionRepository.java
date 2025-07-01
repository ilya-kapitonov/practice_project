package com.equipment.tracker.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.equipment.tracker.entity.EquipmentCondition;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface EquipmentConditionRepository extends JpaRepository<EquipmentCondition, Integer> {
    List<EquipmentCondition> findAll(Sort sort);
    EquipmentCondition findByConditionName(String conditionName);
    boolean existsByConditionName(String conditionName);
}
