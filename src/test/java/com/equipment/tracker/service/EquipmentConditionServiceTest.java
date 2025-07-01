package com.equipment.tracker.service;
import com.equipment.tracker.entity.EquipmentCondition;
import com.equipment.tracker.exception.BadRequestException;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.repository.EquipmentConditionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentConditionServiceTest {

    @Mock
    private EquipmentConditionRepository conditionRepository;

    @InjectMocks
    private EquipmentConditionService conditionService;

    @Test
    void createCondition_Success() {
        EquipmentCondition condition = new EquipmentCondition();
        condition.setConditionName("Новое состояние");

        when(conditionRepository.existsByConditionName("Новое состояние")).thenReturn(false);
        when(conditionRepository.save(condition)).thenAnswer(i -> {
            EquipmentCondition c = i.getArgument(0);
            c.setConditionId(1);
            return c;
        });

        EquipmentCondition created = conditionService.createCondition(condition);

        assertNotNull(created);
        assertEquals(1, created.getConditionId());
        assertEquals("Новое состояние", created.getConditionName());
    }

    @Test
    void createCondition_DuplicateName_ThrowsBadRequest() {
        EquipmentCondition condition = new EquipmentCondition();
        condition.setConditionName("Новое состояние");

        when(conditionRepository.existsByConditionName("Новое состояние")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> conditionService.createCondition(condition));
        assertEquals("Состояние должно быть уникальным", ex.getMessage());
    }

    @Test
    void getConditionById_Found() {
        EquipmentCondition condition = new EquipmentCondition();
        condition.setConditionId(1);
        condition.setConditionName("Исправно");

        when(conditionRepository.findById(1)).thenReturn(Optional.of(condition));

        EquipmentCondition found = conditionService.getConditionById(1);

        assertEquals("Исправно", found.getConditionName());
    }

    @Test
    void getConditionById_NotFound_ThrowsNotFound() {
        when(conditionRepository.findById(1)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> conditionService.getConditionById(1));
        assertTrue(ex.getMessage().contains("Не найдено состояние"));
    }

    @Test
    void updateCondition_Success() {
        EquipmentCondition existing = new EquipmentCondition();
        existing.setConditionId(1);
        existing.setConditionName("Исправно");

        EquipmentCondition updated = new EquipmentCondition();
        updated.setConditionName("Плохо");

        when(conditionRepository.findById(1)).thenReturn(Optional.of(existing));
        when(conditionRepository.existsByConditionName("Плохо")).thenReturn(false);
        when(conditionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        EquipmentCondition result = conditionService.updateCondition(1, updated);

        assertEquals("Плохо", result.getConditionName());
    }

    @Test
    void updateCondition_DuplicateName_ThrowsBadRequest() {
        EquipmentCondition existing = new EquipmentCondition();
        existing.setConditionId(1);
        existing.setConditionName("Исправно");

        EquipmentCondition updated = new EquipmentCondition();
        updated.setConditionName("Плохо");

        when(conditionRepository.findById(1)).thenReturn(Optional.of(existing));
        when(conditionRepository.existsByConditionName("Плохо")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> conditionService.updateCondition(1, updated));
        assertEquals("Состояние должно быть уникальным", ex.getMessage());
    }

    @Test
    void deleteCondition_Success() {
        when(conditionRepository.existsById(1)).thenReturn(true);
        doNothing().when(conditionRepository).deleteById(1);

        assertDoesNotThrow(() -> conditionService.deleteCondition(1));
        verify(conditionRepository).deleteById(1);
    }

    @Test
    void deleteCondition_NotFound_ThrowsNotFound() {
        when(conditionRepository.existsById(1)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> conditionService.deleteCondition(1));
        assertTrue(ex.getMessage().contains("Не найдено состояние"));
    }
}

