package dev.marcosoliveira.discography.api.mapper;

import dev.marcosoliveira.discography.api.dto.AlbumResponseDTO;
import dev.marcosoliveira.discography.application.service.StorageService;
import dev.marcosoliveira.discography.domain.model.Album;
import dev.marcosoliveira.discography.domain.model.Artist;
import dev.marcosoliveira.discography.domain.model.ImageReference;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class AlbumMapper {

    private final StorageService storageService;

    protected AlbumMapper(StorageService storageService) {
        this.storageService = storageService;
    }

    @Mapping(target = "artistNames", source = "artists", qualifiedByName = "mapArtistNames")
    @Mapping(target = "coverUrl", source = "cover", qualifiedByName = "resolveCoverUrl")
    public abstract AlbumResponseDTO toResponseDto(Album album);

    public abstract List<AlbumResponseDTO> toResponseDtoList(List<Album> albums);

    @Named("mapArtistNames")
    protected List<String> mapArtistNames(Set<Artist> artists) {
        if (artists == null) return null;
        return artists.stream()
                .map(Artist::getName)
                .collect(Collectors.toList());
    }

    @Named("resolveCoverUrl")
    protected String resolveCoverUrl(ImageReference cover) {
        if (cover == null) return null;
        return storageService.getUrl(cover);
    }
}
