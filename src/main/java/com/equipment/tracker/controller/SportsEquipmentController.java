package com.equipment.tracker.controller;

import com.equipment.tracker.entity.SportsEquipment;
import com.equipment.tracker.service.SportsEquipmentService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/equipment")
public class SportsEquipmentController {

    private final SportsEquipmentService equipmentService;

    public SportsEquipmentController(SportsEquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    /**
     * Создает новое спортивное оборудование.
     * @param equipment Объект оборудования с вложенными category и condition.
     * @return Созданное оборудование и статус 201 Created.
     */
    @PostMapping
    public ResponseEntity<SportsEquipment> createEquipment(@RequestBody SportsEquipment equipment) {
        Integer categoryId = equipment.getCategory() != null ? equipment.getCategory().getCategoryId() : null;
        Integer conditionId = equipment.getCondition() != null ? equipment.getCondition().getConditionId() : null;

        if (categoryId == null || conditionId == null) {
            return ResponseEntity.badRequest().build();
        }

        SportsEquipment createdEquipment = equipmentService.createEquipment(equipment, categoryId, conditionId);
        return new ResponseEntity<>(createdEquipment, HttpStatus.CREATED);
    }

    /**
     * Получает оборудование по его ID.
     * @param id ID оборудования (UUID).
     * @return Найденное оборудование и статус 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SportsEquipment> getEquipmentById(@PathVariable UUID id) {
        SportsEquipment equipment = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(equipment);
    }

    /**
     * Получает список всего оборудования.
     * Поддерживает сортировку.
     * @param sortBy Поле для сортировки (по умолчанию "serialNumber").
     * @param sortDir Направление сортировки (ASC или DESC, по умолчанию "ASC").
     * @return Список оборудования и статус 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<SportsEquipment>> getAllEquipment(@RequestParam(defaultValue = "serialNumber") String sortBy, @RequestParam(defaultValue = "ASC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        List<SportsEquipment> equipmentList = equipmentService.getAllEquipment(sort);
        return ResponseEntity.ok(equipmentList);
    }

    /**
     * Обновляет существующее оборудование.
     * @param id ID оборудования.
     * @param updatedEquipment Объект с обновленными данными, включая category и condition.
     * @return Обновленное оборудование и статус 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<SportsEquipment> updateEquipment(@PathVariable UUID id, @RequestBody SportsEquipment updatedEquipment) {

        Integer newCategoryId = updatedEquipment.getCategory() != null ? updatedEquipment.getCategory().getCategoryId() : null;
        Integer newConditionId = updatedEquipment.getCondition() != null ? updatedEquipment.getCondition().getConditionId() : null;

        if (newCategoryId == null || newConditionId == null) {
            return ResponseEntity.badRequest().build();
        }

        SportsEquipment equipment = equipmentService.updateEquipment(id, updatedEquipment, newCategoryId, newConditionId);
        return ResponseEntity.ok(equipment);
    }

    /**
     * Удаляет оборудование по его ID.
     * @param id ID оборудования, которое нужно удалить (UUID).
     * @return Статус 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable UUID id) {
        equipmentService.deleteEquipment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
