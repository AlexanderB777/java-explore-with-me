package ru.practicum.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.model.EndpointHit;
import ru.practicum.repository.StatsRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Sql(statements = {
        "INSERT INTO endpoint_hit " +
                "VALUES (1, 'ewm-main-service', '/events/1', '10.10.10.10', TIMESTAMP '2024-11-10 10:00:00')",
        "INSERT INTO endpoint_hit " +
                "VALUES (2, 'ewm-main-service', '/events/2', '10.10.10.10', TIMESTAMP '2024-11-10 11:00:00')",
        "INSERT INTO endpoint_hit " +
                "VALUES (3, 'ewm-main-service', '/events/1', '10.10.10.10', TIMESTAMP '2024-11-10 12:00:00')",
        "INSERT INTO endpoint_hit " +
                "VALUES (4, 'ewm-main-service', '/events/1', '10.10.10.20', TIMESTAMP '2024-11-10 13:00:00')",
        "INSERT INTO endpoint_hit " +
                "VALUES (5, 'ewm-main-service', '/events/1', '10.10.10.30', TIMESTAMP '2024-11-10 14:00:00')",
        "INSERT INTO endpoint_hit " +
                "VALUES (6, 'ewm-main-service', '/events/2', '10.10.10.20', TIMESTAMP '2024-11-10 16:00:00')"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class StatsServiceImplTest {
    @Autowired
    private StatsService statsService;
    @SpyBean
    private StatsRepository statsRepository;

    private static final String START_TIME = "2024-11-10 10:00:00";
    private static final String END_TIME = "2024-11-10 16:00:00";

    @Test
    @Transactional
    @DisplayName("Создание новой записи в базе")
    @Sql(statements = "TRUNCATE TABLE endpoint_hit")
    void testRecordHit() {
        // given
        EndpointHitDto endpointHitDto =
                new EndpointHitDto("ewm-main-service",
                        "/events/1",
                        "10.10.10.10",
                        "2022-09-06 11:00:23");

        // when
        statsService.recordHit(endpointHitDto);

        // then
        verify(statsRepository, times(1)).save(any(EndpointHit.class));
    }

    @Test
    @Transactional
    @DisplayName("Проверка получения записей при uris = null и unique = false")
    void testGetStats_NoUrisAndUniqueFalse() {
        // when
        List<ViewStatsDto> dtoList = statsService.getStats(START_TIME, END_TIME, null, false);

        // then
        assertEquals(2, dtoList.size());
        assertEquals(2, dtoList.getFirst().hits());
        assertEquals(4, dtoList.getLast().hits());
        assertEquals("/events/2", dtoList.getFirst().uri());
        assertEquals("/events/1", dtoList.getLast().uri());
        assertTrue(dtoList.stream().allMatch(x -> x.app().equals("ewm-main-service")));
    }

    @Test
    @Transactional
    @DisplayName("Проверка получения записей при uris = \"/events/1\" и unique = false")
    void testGetStats_UrisAndUniqueFalse() {
        // when
        List<ViewStatsDto> dtoList = statsService.getStats(START_TIME, END_TIME, List.of("/events/1"), false);

        // then
        assertEquals(1, dtoList.size());
        assertEquals("/events/1", dtoList.getFirst().uri());
        assertEquals("ewm-main-service", dtoList.getFirst().app());
        assertEquals(4, dtoList.getFirst().hits());
    }

    @Test
    @Transactional
    @DisplayName("Проверка получения записей при uris = null и unique = true")
    void testGetStats_NoUrisAndUniqueTrue() {
        // when
        List<ViewStatsDto> dtoList = statsService.getStats(START_TIME, END_TIME, null, true);

        // then
        assertEquals(2, dtoList.size());
        assertEquals(3, dtoList.getFirst().hits());
        assertEquals(2, dtoList.getLast().hits());
        assertEquals("/events/1", dtoList.getFirst().uri());
        assertEquals("/events/2", dtoList.getLast().uri());
        assertTrue(dtoList.stream().allMatch(x -> x.app().equals("ewm-main-service")));
    }

    @Test
    @Transactional
    @DisplayName("Проверка получения записей при uris = \"/events/1\" и unique = true")
    void testGetStats_UrisAndUniqueTrue() {
        // when
        List<ViewStatsDto> dtoList = statsService.getStats(START_TIME, END_TIME, List.of("/events/1"), true);

        // then
        assertEquals(1, dtoList.size());
        assertEquals("/events/1", dtoList.getFirst().uri());
        assertEquals(3, dtoList.getFirst().hits());
        assertEquals("ewm-main-service", dtoList.getFirst().app());
    }

    @Test
    @DisplayName("Тест на отсутствующий промежуток времени")
    void testGetStats_NotExistTimestamp() {
        // when
        List<ViewStatsDto> dtoList = statsService.getStats("2025-11-10 10:00:00",
                "2025-11-10 10:00:00",
                null, false);

        // then
        assertEquals(0, dtoList.size());
    }
}