package dev.marcosoliveira.discography.api.mapper;

import dev.marcosoliveira.discography.api.dto.ArtistResponseDTO;
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
public abstract class ArtistMapper {

    @Autowired
    protected StorageService storageService;

    @Mapping(target = "albumTitles", source = "albums", qualifiedByName = "mapAlbumTitles")
    @Mapping(target = "profileImageUrl", source = "profileImage", qualifiedByName = "resolveProfileImageUrl")
    public abstract ArtistResponseDTO toResponseDto(Artist artist);

    public abstract List<ArtistResponseDTO> toResponseDtoList(List<Artist> artists);

    @Named("mapAlbumTitles")
    protected List<String> mapAlbumTitles(Set<Album> albums) {
        if (albums == null) return null;
        return albums.stream()
                .map(Album::getTitle)
                .collect(Collectors.toList());
    }

    @Named("resolveProfileImageUrl")
    protected String resolveProfileImageUrl(ImageReference profileImage) {
        if (profileImage == null) return null;
        return storageService.getUrl(profileImage);
    }
}
