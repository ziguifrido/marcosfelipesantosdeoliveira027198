package dev.marcosoliveira.discography.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.marcosoliveira.discography.api.dto.AlbumRequestDTO;
import dev.marcosoliveira.discography.api.dto.AlbumResponseDTO;
import dev.marcosoliveira.discography.api.mapper.AlbumMapper;
import dev.marcosoliveira.discography.application.service.AlbumService;
import dev.marcosoliveira.discography.domain.exception.ResourceNotFoundException;
import dev.marcosoliveira.discography.domain.model.Album;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlbumController.class)
class AlbumControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AlbumService albumService;

    @MockitoBean
    private AlbumMapper albumMapper;

    @Test
    void findById_shouldReturnOk() throws Exception {
        UUID id = UUID.randomUUID();
        Album album = mock(Album.class);
        AlbumResponseDTO responseDTO = new AlbumResponseDTO();
        responseDTO.setId(id);
        responseDTO.setTitle("Live in Texas");
        responseDTO.setReleaseDate(LocalDate.of(2003, 11, 18));
        responseDTO.setArtistNames(List.of("Linkin Park"));

        when(albumService.findById(id)).thenReturn(album);
        when(albumMapper.toResponseDto(album)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/v1/albums/{id}",id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.title").value("Live in Texas"))
                .andExpect(jsonPath("$.releaseDate").value("2003-11-18"))
                .andExpect(jsonPath("$.artistNames").value("Linkin Park"));

    }

    @Test
    void findById_WhenNotFound_ShouldReturn404() throws Exception{
        UUID id = UUID.randomUUID();
        when(albumService.findById(id)).thenThrow(new ResourceNotFoundException("Album",id));

        mockMvc.perform(get("/api/v1/albums/{id}",id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Resource Not Found"));
    }

    @Test
    void createAlbum_WithImage_ShouldSucceed() throws Exception {
        UUID albumId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();

        AlbumRequestDTO requestDTO = new AlbumRequestDTO();
        requestDTO.setTitle("Live in Texas");
        requestDTO.setReleaseDate(LocalDate.of(2003, 11, 18));
        requestDTO.setArtistIds(List.of(artistId));

        Album album = mock(Album.class);
        when(album.getId()).thenReturn(albumId);

        AlbumResponseDTO responseDTO = new AlbumResponseDTO();
        responseDTO.setId(albumId);
        responseDTO.setTitle("Live in Texas");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "request",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDTO)
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "cover.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        when(albumService.createAlbum(any(), any(), any())).thenReturn(album);
        when(albumMapper.toResponseDto(any())).thenReturn(responseDTO);

        mockMvc.perform(multipart("/api/v1/albums")
                        .file(jsonPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(albumId.toString()))
                .andExpect(jsonPath("$.title").value("Live in Texas"));

        verify(albumService).createAlbum(
                eq("Live in Texas"),
                eq(LocalDate.of(2003, 11, 18)),
                eq(List.of(artistId)));

        verify(albumService).uploadAlbumCover(eq(albumId), any(), eq(MediaType.IMAGE_JPEG_VALUE));
    }
}