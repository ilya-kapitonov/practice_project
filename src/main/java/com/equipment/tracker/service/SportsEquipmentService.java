package com.equipment.tracker.service;

import com.equipment.tracker.entity.*;
import com.equipment.tracker.exception.*;
import com.equipment.tracker.repository.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SportsEquipmentService {
    private static final Logger logger = LoggerFactory.getLogger(SportsEquipmentService.class);

    private final SportsEquipmentRepository equipmentRepository;
    private final CategoryRepository categoryRepository;
    private final EquipmentConditionRepository conditionRepository;

    public SportsEquipmentService(SportsEquipmentRepository equipmentRepository, CategoryRepository categoryRepository, EquipmentConditionRepository conditionRepository) {
        this.equipmentRepository = equipmentRepository;
        this.categoryRepository = categoryRepository;
        this.conditionRepository = conditionRepository;
    }

    public SportsEquipment createEquipment(@Valid SportsEquipment equipment, Integer categoryId, Integer conditionId) {
        validateEquipment(equipment);

        if (categoryId == null) {
            throw new BadRequestException("ID категории не может быть null");
        }
        if (conditionId == null) {
            throw new BadRequestException("ID состояния не может быть null");
        }

        Category category = getCategoryById(categoryId);
        EquipmentCondition condition = getConditionById(conditionId);

        if (equipmentRepository.existsBySerialNumber(equipment.getSerialNumber())) {
            throw new BadRequestException("Серийный номер должен быть уникальным");
        }

        equipment.setCategory(category);
        equipment.setCondition(condition);

        logger.info("Создание оборудования: {}", equipment);
        SportsEquipment saved = equipmentRepository.save(equipment);
        logger.info("Оборудование сохранено с id: {}", saved.getEquipmentId());
        return saved;
    }

    public List<SportsEquipment> getAllEquipment(Sort sort) {
        return equipmentRepository.findAll(sort);
    }

    public SportsEquipment getEquipmentById(UUID id) {
        if (id == null) {
            throw new BadRequestException("ID оборудования не может быть null");
        }
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено оборудование с id: " + id));
    }

    public SportsEquipment updateEquipment(UUID id, @Valid SportsEquipment updatedEquipment, Integer newCategoryId, Integer newConditionId) {
        if (id == null) {
            throw new BadRequestException("ID оборудования не может быть null");
        }
        if (newCategoryId == null) {
            throw new BadRequestException("ID категории не может быть null");
        }
        if (newConditionId == null) {
            throw new BadRequestException("ID состояния не может быть null");
        }

        validateEquipment(updatedEquipment);

        SportsEquipment existingEquipment = getEquipmentById(id);
        Category newCategory = getCategoryById(newCategoryId);
        EquipmentCondition newCondition = getConditionById(newConditionId);

        if (!existingEquipment.getSerialNumber().equals(updatedEquipment.getSerialNumber())) {
            if (equipmentRepository.existsBySerialNumber(updatedEquipment.getSerialNumber())) {
                throw new BadRequestException("Серийный номер должен быть уникальным");
            }
        }

        existingEquipment.setSerialNumber(updatedEquipment.getSerialNumber());
        existingEquipment.setEquipmentName(updatedEquipment.getEquipmentName());
        existingEquipment.setQuantity(updatedEquipment.getQuantity());
        existingEquipment.setDescription(updatedEquipment.getDescription());
        existingEquipment.setLastCheckDate(updatedEquipment.getLastCheckDate());
        existingEquipment.setEndOfServiceDate(updatedEquipment.getEndOfServiceDate());
        existingEquipment.setCategory(newCategory);
        existingEquipment.setCondition(newCondition);

        SportsEquipment saved = equipmentRepository.save(existingEquipment);
        logger.info("Обновлено оборудование с id {}", saved.getEquipmentId());
        return saved;
    }

    public void deleteEquipment(UUID id) {
        if (id == null) {
            throw new BadRequestException("ID оборудования не может быть null");
        }
        if (!equipmentRepository.existsById(id)) {
            throw new NotFoundException("Не найдено оборудование с id: " + id);
        }
        equipmentRepository.deleteById(id);
        logger.info("Удалено оборудование с id {}", id);
    }

    private Category getCategoryById(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория не найдена с id: " + id));
    }

    private EquipmentCondition getConditionById(Integer id) {
        return conditionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Состояние не найдено с id: " + id));
    }

    private void validateEquipment(SportsEquipment equipment) {
        if (equipment == null) {
            throw new BadRequestException("Оборудование не может быть null");
        }
        String serial = equipment.getSerialNumber();
        String equipmentName = equipment.getEquipmentName();
        if (serial == null || serial.trim().isEmpty()) {
            throw new BadRequestException("Серийный номер не может быть пустым");
        }
        if (serial.length() > 50) {
            throw new BadRequestException("Серийный номер не может быть длиннее 50 символов");
        }
        if (equipmentName == null || equipmentName.trim().isEmpty()) {
            throw new BadRequestException("Название не может быть пустым");
        }
        if (equipmentName.length() > 100) {
            throw new BadRequestException("Название не может быть длиннее 100 символов");
        }
        if (equipment.getQuantity() == null || equipment.getQuantity() < 0) {
            throw new BadRequestException("Количество должно быть больше или равно 0");
        }
        if (equipment.getEndOfServiceDate() == null) {
            throw new BadRequestException("Дата окончания срока службы обязательна");
        }
    }
}

