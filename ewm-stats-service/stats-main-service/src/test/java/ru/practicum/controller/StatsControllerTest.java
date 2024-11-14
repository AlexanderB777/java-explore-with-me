package ru.practicum.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatsService statsService;


    @Test
    void testRecordHit() throws Exception {
        EndpointHitDto endpointHitDto =
                new EndpointHitDto("app1", "/home", "192.168.0.1", "2024-11-10 14:00:00");

        mockMvc.perform(post("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(endpointHitDto)))
                .andExpect(status().isOk());
        Mockito.verify(statsService, times(1)).recordHit(any(EndpointHitDto.class));
    }

    @Test
    void testGetStats() throws Exception{
        ViewStatsDto viewStatsDto = new ViewStatsDto("app1", "/home", 100L);
        List<ViewStatsDto> statsList = Collections.singletonList(viewStatsDto);

        Mockito.when(statsService.getStats(anyString(), anyString(), anyList(), anyBoolean()))
                .thenReturn(statsList);

        mockMvc.perform(get("/stats")
                        .param("start", "2023-01-01+00%3A00%3A00")
                        .param("end", "2023-12-31+23%3A59%3A59")
                        .param("uris", "")
                        .param("unique", "false"))
                .andExpect(status().isOk());

        Mockito
                .verify(statsService, times(1))
                .getStats(anyString(), anyString(), anyList(), anyBoolean());
    }
}