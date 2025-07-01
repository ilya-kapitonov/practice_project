package com.equipment.tracker.controller;
import com.equipment.tracker.entity.EquipmentCondition;
import com.equipment.tracker.exception.NotFoundException;
import com.equipment.tracker.service.EquipmentConditionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EquipmentConditionController.class)
class EquipmentConditionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EquipmentConditionService conditionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createCondition_Success() throws Exception {
        EquipmentCondition condition = new EquipmentCondition();
        condition.setConditionId(1);
        condition.setConditionName("Исправно");

        when(conditionService.createCondition(any(EquipmentCondition.class))).thenReturn(condition);

        mockMvc.perform(post("/api/conditions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(condition)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.conditionId").value(1))
                .andExpect(jsonPath("$.conditionName").value("Исправно"));
    }

    @Test
    void getConditionById_Success() throws Exception {
        EquipmentCondition condition = new EquipmentCondition();
        condition.setConditionId(1);
        condition.setConditionName("Исправно");

        when(conditionService.getConditionById(1)).thenReturn(condition);

        mockMvc.perform(get("/api/conditions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conditionName").value("Исправно"));
    }

    @Test
    void getConditionById_NotFound() throws Exception {
        when(conditionService.getConditionById(1)).thenThrow(new NotFoundException("Не найдено состояние"));

        mockMvc.perform(get("/api/conditions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllConditions_Success() throws Exception {
        EquipmentCondition c1 = new EquipmentCondition();
        c1.setConditionId(1);
        c1.setConditionName("Исправно");
        EquipmentCondition c2 = new EquipmentCondition();
        c2.setConditionId(2);
        c2.setConditionName("Плохо");

        when(conditionService.getAllConditions(any(Sort.class))).thenReturn(List.of(c1, c2));

        mockMvc.perform(get("/api/conditions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void updateCondition_Success() throws Exception {
        EquipmentCondition updated = new EquipmentCondition();
        updated.setConditionId(1);
        updated.setConditionName("Плохо");

        when(conditionService.updateCondition(eq(1), any(EquipmentCondition.class))).thenReturn(updated);

        mockMvc.perform(put("/api/conditions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updated)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conditionName").value("Плохо"));
    }

    @Test
    void deleteCondition_Success() throws Exception {
        doNothing().when(conditionService).deleteCondition(1);

        mockMvc.perform(delete("/api/conditions/1"))
                .andExpect(status().isNoContent());
    }
}
