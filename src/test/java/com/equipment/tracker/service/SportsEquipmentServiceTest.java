package com.equipment.tracker.service;
import com.equipment.tracker.entity.*;
import com.equipment.tracker.exception.BadRequestException;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SportsEquipmentServiceTest {

    @Mock
    private SportsEquipmentRepository equipmentRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private EquipmentConditionRepository conditionRepository;

    @InjectMocks
    private SportsEquipmentService equipmentService;

    private Category createCategory() {
        Category c = new Category();
        c.setCategoryId(1);
        c.setCategoryName("Спорт");
        return c;
    }

    private EquipmentCondition createCondition() {
        EquipmentCondition c = new EquipmentCondition();
        c.setConditionId(1);
        c.setConditionName("Исправно");
        return c;
    }

    private SportsEquipment createEquipment() {
        SportsEquipment e = new SportsEquipment();
        e.setSerialNumber("SN123");
        e.setEquipmentName("Мяч");
        e.setQuantity(10);
        e.setEndOfServiceDate(LocalDate.now().plusDays(365));
        e.setCategory(createCategory());
        e.setCondition(createCondition());
        return e;
    }

    @Test
    void createEquipment_Success() {
        SportsEquipment equipment = createEquipment();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(createCategory()));
        when(conditionRepository.findById(1)).thenReturn(Optional.of(createCondition()));
        when(equipmentRepository.existsBySerialNumber("SN123")).thenReturn(false);
        when(equipmentRepository.save(any())).thenAnswer(i -> {
            SportsEquipment e = i.getArgument(0);
            return e;
        });

        SportsEquipment created = equipmentService.createEquipment(equipment, 1, 1);

        assertNotNull(created.getEquipmentId());
        assertEquals("SN123", created.getSerialNumber());
        verify(equipmentRepository).save(any());
    }

    @Test
    void createEquipment_DuplicateSerial_ThrowsBadRequest() {
        SportsEquipment equipment = createEquipment();

        when(categoryRepository.findById(1)).thenReturn(Optional.of(createCategory()));
        when(conditionRepository.findById(1)).thenReturn(Optional.of(createCondition()));
        when(equipmentRepository.existsBySerialNumber("SN123")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> equipmentService.createEquipment(equipment, 1, 1));
        assertEquals("Серийный номер должен быть уникальным", ex.getMessage());
    }

    @Test
    void getEquipmentById_Found() {
        SportsEquipment equipment = createEquipment();

        when(equipmentRepository.findById(equipment.getEquipmentId())).thenReturn(Optional.of(equipment));

        SportsEquipment found = equipmentService.getEquipmentById(equipment.getEquipmentId());

        assertEquals(equipment.getSerialNumber(), found.getSerialNumber());
    }

    @Test
    void getEquipmentById_NotFound_ThrowsNotFound() {
        UUID id = UUID.randomUUID();

        when(equipmentRepository.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> equipmentService.getEquipmentById(id));
        assertTrue(ex.getMessage().contains("Не найдено оборудование"));
    }

    @Test
    void updateEquipment_Success() {
        SportsEquipment existing = createEquipment();
        UUID id = existing.getEquipmentId();

        SportsEquipment updated = createEquipment();
        updated.setSerialNumber("SN456");
        updated.setEquipmentName("Мяч обновлённый");
        updated.setQuantity(20);
        updated.setEndOfServiceDate(LocalDate.now().plusDays(500));

        when(equipmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(createCategory()));
        when(conditionRepository.findById(1)).thenReturn(Optional.of(createCondition()));
        when(equipmentRepository.existsBySerialNumber("SN456")).thenReturn(false);
        when(equipmentRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        SportsEquipment result = equipmentService.updateEquipment(id, updated, 1, 1);

        assertEquals("SN456", result.getSerialNumber());
        assertEquals("Мяч обновлённый", result.getEquipmentName());
        assertEquals(20, result.getQuantity());
    }

    @Test
    void updateEquipment_DuplicateSerial_ThrowsBadRequest() {
        SportsEquipment existing = createEquipment();
        UUID id = existing.getEquipmentId();

        SportsEquipment updated = createEquipment();
        updated.setSerialNumber("SN456");

        when(equipmentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(1)).thenReturn(Optional.of(createCategory()));
        when(conditionRepository.findById(1)).thenReturn(Optional.of(createCondition()));
        when(equipmentRepository.existsBySerialNumber("SN456")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> equipmentService.updateEquipment(id, updated, 1, 1));
        assertEquals("Серийный номер должен быть уникальным", ex.getMessage());
    }

    @Test
    void deleteEquipment_Success() {
        UUID id = UUID.randomUUID();

        when(equipmentRepository.existsById(id)).thenReturn(true);
        doNothing().when(equipmentRepository).deleteById(id);

        assertDoesNotThrow(() -> equipmentService.deleteEquipment(id));
        verify(equipmentRepository).deleteById(id);
    }

    @Test
    void deleteEquipment_NotFound_ThrowsNotFound() {
        UUID id = UUID.randomUUID();

        when(equipmentRepository.existsById(id)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> equipmentService.deleteEquipment(id));
        assertTrue(ex.getMessage().contains("Не найдено оборудование"));
    }
}

