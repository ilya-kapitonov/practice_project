package com.equipment.tracker.controller;

import com.equipment.tracker.entity.EquipmentCondition;
import com.equipment.tracker.service.EquipmentConditionService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conditions")
public class EquipmentConditionController {

    private final EquipmentConditionService conditionService;

    public EquipmentConditionController(EquipmentConditionService conditionService) {
        this.conditionService = conditionService;
    }

    /**
     * Создает новое состояние оборудования.
     * @param condition Объект состояния, полученный из тела запроса.
     * @return Созданное состояние и статус 201 Created.
     */
    @PostMapping
    public ResponseEntity<EquipmentCondition> createCondition(@RequestBody EquipmentCondition condition) {
        EquipmentCondition createdCondition = conditionService.createCondition(condition);
        return new ResponseEntity<>(createdCondition, HttpStatus.CREATED);
    }

    /**
     * Получает состояние оборудования по его ID.
     * @param id ID состояния.
     * @return Найденное состояние и статус 200 OK.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EquipmentCondition> getConditionById(@PathVariable Integer id) {
        EquipmentCondition condition = conditionService.getConditionById(id);
        return ResponseEntity.ok(condition);
    }

    /**
     * Получает список всех состояний оборудования.
     * Поддерживает сортировку по имени состояния.
     * @param sortBy Поле для сортировки (по умолчанию "conditionName").
     * @param sortDir Направление сортировки (ASC или DESC, по умолчанию "ASC").
     * @return Список состояний и статус 200 OK.
     */
    @GetMapping
    public ResponseEntity<List<EquipmentCondition>> getAllConditions(@RequestParam(defaultValue = "conditionName") String sortBy, @RequestParam(defaultValue = "ASC") String sortDir) {
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        List<EquipmentCondition> conditions = conditionService.getAllConditions(sort);
        return ResponseEntity.ok(conditions);
    }

    /**
     * Обновляет существующее состояние оборудования.
     * @param id ID состояния, которое нужно обновить.
     * @param updatedCondition Объект состояния с обновленными данными.
     * @return Обновленное состояние и статус 200 OK.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EquipmentCondition> updateCondition(@PathVariable Integer id, @RequestBody EquipmentCondition updatedCondition) {
        EquipmentCondition condition = conditionService.updateCondition(id, updatedCondition);
        return ResponseEntity.ok(condition);
    }

    /**
     * Удаляет состояние оборудования по его ID.
     * @param id ID состояния, которое нужно удалить.
     * @return Статус 204 No Content.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCondition(@PathVariable Integer id) {
        conditionService.deleteCondition(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
