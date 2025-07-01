package com.equipment.tracker.controller;
import com.equipment.tracker.entity.Category;
import com.equipment.tracker.entity.EquipmentCondition;
import com.equipment.tracker.entity.SportsEquipment;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.service.SportsEquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SportsEquipmentController.class)
class SportsEquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SportsEquipmentService equipmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private SportsEquipment createEquipment() {
        SportsEquipment e = new SportsEquipment();
        e.setSerialNumber("SN123");
        e.setEquipmentName("Мяч");

        Category category = new Category();
        category.setCategoryId(1);
        category.setCategoryName("Спорт");
        e.setCategory(category);

        EquipmentCondition condition = new EquipmentCondition();
        condition.setConditionId(1);
        condition.setConditionName("Исправно");
        e.setCondition(condition);

        return e;
    }

    @Test
    void createEquipment_Success() throws Exception {
        SportsEquipment equipment = createEquipment();
        when(equipmentService.createEquipment(any(), anyInt(), anyInt())).thenReturn(equipment);

        mockMvc.perform(post("/api/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipment)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serialNumber").value("SN123"))
                .andExpect(jsonPath("$.equipmentName").value("Мяч"));
    }

    @Test
    void createEquipment_BadRequest_MissingCategoryOrCondition() throws Exception {
        SportsEquipment equipment = createEquipment();
        equipment.setCategory(null); // нет категории

        mockMvc.perform(post("/api/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEquipmentById_Success() throws Exception {
        SportsEquipment equipment = createEquipment();
        UUID id = equipment.getEquipmentId();

        when(equipmentService.getEquipmentById(id)).thenReturn(equipment);

        mockMvc.perform(get("/api/equipment/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber").value("SN123"));
    }

    @Test
    void getEquipmentById_NotFound() throws Exception {
        UUID id = UUID.randomUUID();

        when(equipmentService.getEquipmentById(id)).thenThrow(new NotFoundException("Не найдено оборудование"));

        mockMvc.perform(get("/api/equipment/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllEquipment_Success() throws Exception {
        SportsEquipment e1 = createEquipment();
        SportsEquipment e2 = createEquipment();
        e2.setSerialNumber("SN456");

        when(equipmentService.getAllEquipment(any(Sort.class))).thenReturn(List.of(e1, e2));

        mockMvc.perform(get("/api/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateEquipment_Success() throws Exception {
        SportsEquipment equipment = createEquipment();
        UUID id = equipment.getEquipmentId();

        when(equipmentService.updateEquipment(eq(id), any(), anyInt(), anyInt())).thenReturn(equipment);

        mockMvc.perform(put("/api/equipment/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(equipment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serialNumber").value("SN123"));
    }

    @Test
    void deleteEquipment_Success() throws Exception {
        doNothing().when(equipmentService).deleteEquipment(any());

        mockMvc.perform(delete("/api/equipment/" + UUID.randomUUID()))
                .andExpect(status().isNoContent());
    }
}
