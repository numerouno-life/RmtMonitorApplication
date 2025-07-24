package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.AggregateDTO;
import ru.practicum.enums.AggregateType;
import ru.practicum.model.Aggregate;
import ru.practicum.repository.AggregateRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AggregateControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private AggregateRepository aggregateRepository;

    @BeforeEach
    void setUp() {
        aggregateRepository.deleteAll();
    }

    private Aggregate createAggregate(String name, AggregateType type, boolean hasTemperatureSensors) {
        return aggregateRepository.save(Aggregate.builder()
                .name(name)
                .type(type)
                .hasTemperatureSensors(hasTemperatureSensors)
                .build());
    }

    private String toJson(Object obj) throws Exception {
        return mapper.writeValueAsString(obj);
    }

    @Test
    void testCreateAggregate() throws Exception {
        AggregateDTO dto = new AggregateDTO(null, "Test Aggregate", AggregateType.VD_18, true);

        mockMvc.perform(post("/aggregates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Test Aggregate"));
    }

    @Test
    void testDeleteAggregateById() throws Exception {
        Aggregate saved = createAggregate("ToDelete", AggregateType.VD_18, true);

        mockMvc.perform(delete("/aggregates/{id}", saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetAllAggregates() throws Exception {
        for (int i = 0; i < 5; i++) {
            createAggregate("Test Aggregate " + i, AggregateType.VM_40, true);
        }
        mockMvc.perform(get("/aggregates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(5))
                .andExpect(jsonPath("$[0].name").value("Test Aggregate 0"))
                .andExpect(jsonPath("$[4].name").value("Test Aggregate 4"));
    }

    @Test
    void testGetAggregateById() throws Exception {
        Aggregate saved = createAggregate("ById Agg", AggregateType.VD_18, true);

        mockMvc.perform(get("/aggregates/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ById Agg"));
    }

    @Test
    void testUpdateAggregate() throws Exception {
        Aggregate saved = createAggregate("Old Name", AggregateType.VD_18, true);

        AggregateDTO updateDto = new AggregateDTO(saved.getId(), "New Name", AggregateType.VM_40, false);

        mockMvc.perform(patch("/aggregates/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.type").value("VM_40"))
                .andExpect(jsonPath("$.hasTemperatureSensors").value(false));
    }

    @Test
    void testGetAggregatesByName() throws Exception {
        Aggregate findByName = createAggregate("Aggregate Name", AggregateType.VD_18, true);

        mockMvc.perform(get("/aggregates/search/by-name")
                        .param("name", findByName.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Aggregate Name"));
    }

    @Test
    void testGetAggregatesByType() throws Exception {
        Aggregate findByType = createAggregate("VD_18", AggregateType.VD_18, true);

        mockMvc.perform(get("/aggregates/search/by-type")
                        .param("type", findByType.getType().toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("VD_18"));
    }

    @Test
    void testGetAggregateByIdNotFound() throws Exception {
        mockMvc.perform(get("/aggregates/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAggregateByIdNotFound() throws Exception {
        mockMvc.perform(delete("/aggregates/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateAggregateWithInvalidName() throws Exception {
        AggregateDTO dto = new AggregateDTO(null, "", AggregateType.VD_18, true);

        mockMvc.perform(post("/aggregates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAggregatesByInvalidType() throws Exception {
        mockMvc.perform(get("/aggregates/search/by-type")
                        .param("type", "INVALID_TYPE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateAggregateWithDuplicateName() throws Exception {
        AggregateDTO firstDto = new AggregateDTO(null, "Duplicate Name", AggregateType.VD_18, true);

        mockMvc.perform(post("/aggregates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(firstDto)))
                .andExpect(status().isCreated());

        AggregateDTO duplicateDto = new AggregateDTO(null, "Duplicate Name", AggregateType.VM_40, true);

        mockMvc.perform(post("/aggregates")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(duplicateDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Unable to create aggregate due to conflict"))
                .andExpect(jsonPath("$.reason").value("Duplicate resource"));
    }

    @Test
    void testGetAggregatesByNameNotFound() throws Exception {
        mockMvc.perform(get("/aggregates/search/by-name")
                        .param("name", "NonExistentName"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void testGetAggregatesByNameWithSpecialCharacters() throws Exception {
        createAggregate("Test@#$%Name", AggregateType.VD_18, true);
        mockMvc.perform(get("/aggregates/search/by-name")
                        .param("name", "Test@#$%Name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test@#$%Name"));
    }

    @Test
    void testUpdateAggregateWithPartialData() throws Exception {
        Aggregate saved = createAggregate("Old Name", AggregateType.VD_18, true);

        String partialUpdate = "{\"name\":\"Updated Name\"}";

        mockMvc.perform(patch("/aggregates/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(partialUpdate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.type").value("VD_18"))
                .andExpect(jsonPath("$.hasTemperatureSensors").value(true));
    }

}
