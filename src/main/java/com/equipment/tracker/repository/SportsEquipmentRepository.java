package com.equipment.tracker.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.equipment.tracker.entity.SportsEquipment;
import java.util.UUID;
import java.util.List;
import org.springframework.data.domain.Sort;

public interface SportsEquipmentRepository extends JpaRepository<SportsEquipment, UUID> {
    List<SportsEquipment> findAll(Sort sort);
    List<SportsEquipment> findByCategory_CategoryId(Integer categoryId);
    List<SportsEquipment> findByCondition_ConditionId(Integer conditionId);
    SportsEquipment findBySerialNumber(String serialNumber);
    boolean existsBySerialNumber(String serialNumber);
}
