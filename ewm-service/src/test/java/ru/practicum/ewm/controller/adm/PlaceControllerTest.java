package ru.practicum.ewm.controller.adm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.dto.place.NewPlaceDto;
import ru.practicum.ewm.dto.place.PlaceFullDto;
import ru.practicum.ewm.service.PlaceService;
import ru.practicum.stats.client.HitClient;
import ru.practicum.stats.client.StatsClient;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.ewm.HelperLocationTestEntity.getLocationDtoWithoutName;
import static ru.practicum.ewm.HelperLocationTestEntity.getLocationFullDto;
import static ru.practicum.ewm.HelperLocationTestEntity.getNewLocationDto;

@WebMvcTest(controllers = PlaceController.class)
class PlaceControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    PlaceService placeService;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private HitClient hitClient;

    @MockBean
    private StatsClient statsClient;


    private NewPlaceDto newPlaceDto = getNewLocationDto();

    private PlaceFullDto locationFull = getLocationFullDto();

    private NewPlaceDto locationDtoWithoutName = getLocationDtoWithoutName();


    @Test
    void shouldAddLocation() throws Exception {
        when(placeService.addLocation(newPlaceDto))
                .thenReturn(locationFull);


        mvc.perform(post("/admin/locations")
                        .content(mapper.writeValueAsString(locationFull))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(locationFull.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(locationFull.getName())))
                .andExpect(jsonPath("$.lat", is(12.33)))
                .andExpect(jsonPath("$.lon", is(17.18)))
                .andExpect(jsonPath("$.radius", is(locationFull.getRadius())));

        verify(placeService, times(1)).addLocation(newPlaceDto);

    }


    @Test
    void shouldGetLocation() throws Exception {
        List<PlaceFullDto> locations = List.of(locationFull);
        when(placeService.getLocations(0, 10))
                .thenReturn(locations);


        mvc.perform(get("/admin/locations")
                        .content(mapper.writeValueAsString(locations))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(locations)));

        verify(placeService, times(1)).getLocations(0, 10);

    }

    @Test
    void shouldGetLocationById() throws Exception {
        when(placeService.getLocationById(1))
                .thenReturn(locationFull);


        mvc.perform(get("/admin/locations/1")
                        .content(mapper.writeValueAsString(locationFull))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(locationFull.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(locationFull.getName())))
                .andExpect(jsonPath("$.lat", is(12.33)))
                .andExpect(jsonPath("$.lon", is(17.18)))
                .andExpect(jsonPath("$.radius", is(locationFull.getRadius())));

        verify(placeService, times(1)).getLocationById(1);
    }

}