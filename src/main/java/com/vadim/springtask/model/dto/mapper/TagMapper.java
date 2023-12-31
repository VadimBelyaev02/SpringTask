package com.vadim.springtask.model.dto.mapper;

import com.vadim.springtask.model.dto.request.TagRequestDto;
import com.vadim.springtask.model.dto.response.TagResponseDto;
import com.vadim.springtask.model.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TagMapper {

    Tag toEntity(TagRequestDto dto);

    TagResponseDto toResponseDto(Tag tag);

    List<Tag> tagRequestDtoListToTagList(List<TagRequestDto> tagRequestDtos);

    List<TagResponseDto> tagListToTagResponseDtoList(List<Tag> tags);

    void updateTagFromDto(TagRequestDto requestDto, @MappingTarget Tag tag);

}
