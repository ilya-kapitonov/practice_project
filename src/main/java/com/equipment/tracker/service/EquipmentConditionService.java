package com.equipment.tracker.service;

import com.equipment.tracker.entity.EquipmentCondition;
import com.equipment.tracker.exception.BadRequestException;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.repository.EquipmentConditionRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentConditionService {
    private static final Logger logger = LoggerFactory.getLogger(EquipmentConditionService.class);

    private final EquipmentConditionRepository conditionRepository;

    public EquipmentConditionService(EquipmentConditionRepository conditionRepository) {
        this.conditionRepository = conditionRepository;
    }

    public EquipmentCondition createCondition(@Valid EquipmentCondition condition) {
        validateCondition(condition);
        if (conditionRepository.existsByConditionName(condition.getConditionName())) {
            throw new BadRequestException("Состояние должно быть уникальным");
        }
        EquipmentCondition saved = conditionRepository.save(condition);
        logger.info("Создано состояние с id {}", saved.getConditionId());
        return saved;
    }

    public EquipmentCondition getConditionById(Integer id) {
        if (id == null) {
            throw new BadRequestException("ID состояния не может быть null");
        }
        return conditionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Не найдено состояние с id: " + id));
    }

    public List<EquipmentCondition> getAllConditions(Sort sort) {
        return conditionRepository.findAll(sort);
    }

    public EquipmentCondition updateCondition(Integer id, @Valid EquipmentCondition updatedCondition) {
        if (id == null) {
            throw new BadRequestException("ID состояния не может быть null");
        }
        validateCondition(updatedCondition);
        EquipmentCondition existingCondition = getConditionById(id);

        if (!existingCondition.getConditionName().equals(updatedCondition.getConditionName())) {
            if (conditionRepository.existsByConditionName(updatedCondition.getConditionName())) {
                throw new BadRequestException("Состояние должно быть уникальным");
            }
        }

        existingCondition.setConditionName(updatedCondition.getConditionName());
        EquipmentCondition saved = conditionRepository.save(existingCondition);
        logger.info("Обновлено состояние с id {}", saved.getConditionId());
        return saved;
    }

    public void deleteCondition(Integer id) {
        if (id == null) {
            throw new BadRequestException("ID состояния не может быть null");
        }
        if (!conditionRepository.existsById(id)) {
            throw new NotFoundException("Не найдено состояние с id: " + id);
        }
        conditionRepository.deleteById(id);
        logger.info("Удалено состояние с id {}", id);
    }

    private void validateCondition(EquipmentCondition condition) {
        if (condition == null) {
            throw new BadRequestException("Состояние не может быть null");
        }
        String name = condition.getConditionName();
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Название состояния не может быть пустым");
        }
        if (name.length() > 100) {
            throw new BadRequestException("Название состояния не может быть длиннее 100 символов");
        }
    }
}


