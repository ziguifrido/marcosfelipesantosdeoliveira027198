package dev.marcosoliveira.discography.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.marcosoliveira.discography.api.dto.ArtistRequestDTO;
import dev.marcosoliveira.discography.api.dto.ArtistResponseDTO;
import dev.marcosoliveira.discography.api.mapper.ArtistMapper;
import dev.marcosoliveira.discography.application.service.ArtistService;
import dev.marcosoliveira.discography.domain.exception.ResourceNotFoundException;
import dev.marcosoliveira.discography.domain.model.Artist;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import dev.marcosoliveira.discography.TestConfig;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ArtistController.class)
@Import(TestConfig.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class ArtistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ArtistService artistService;

    @MockitoBean
    private ArtistMapper artistMapper;

    @Test
    void findById_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        Artist artist = mock(Artist.class);
        ArtistResponseDTO responseDTO = new ArtistResponseDTO();
        responseDTO.setId(id);
        responseDTO.setName("Linkin Park");
        responseDTO.setGenre("Rock");

        when(artistService.findById(id)).thenReturn(artist);
        when(artistMapper.toResponseDto(artist)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/artists/{id}",id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Linkin Park"))
                .andExpect(jsonPath("$.genre").value("Rock"));
    }

    @Test
    void findById_WhenNotFound_ShouldReturn404() throws Exception{
        UUID id = UUID.randomUUID();
        when(artistService.findById(id)).thenThrow(new ResourceNotFoundException("Artist",id));

        mockMvc.perform(get("/api/v1/artists/{id}",id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }
    
    @Test
    void createArtist_WithImage_ShouldSucceed() throws Exception {
        UUID artistId = UUID.randomUUID();
        ArtistRequestDTO requestDto = new ArtistRequestDTO();
        requestDto.setName("Linkin Park");
        requestDto.setGenre("Rock");

        Artist artist = mock(Artist.class);
        when(artist.getId()).thenReturn(artistId);

        ArtistResponseDTO responseDto = new ArtistResponseDTO();
        responseDto.setId(artistId);
        responseDto.setName("Linkin Park");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        when(artistService.createArtist(any(), any())).thenReturn(artist);
        when(artistMapper.toResponseDto(any())).thenReturn(responseDto);

        mockMvc.perform(multipart("/api/v1/artists")
                        .file(jsonPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Linkin Park"));

        verify(artistService).createArtist(eq("Linkin Park"), eq("Rock"));
        verify(artistService).uploadArtistProfileImage(eq(artistId), any(), eq(MediaType.IMAGE_JPEG_VALUE));
    }
}