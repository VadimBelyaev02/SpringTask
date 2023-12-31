package com.vadim.springtask.service.impl;

import com.vadim.springtask.dao.TagDao;
import com.vadim.springtask.exception.DuplicateRecordException;
import com.vadim.springtask.exception.NotFoundException;
import com.vadim.springtask.model.dto.mapper.TagMapper;
import com.vadim.springtask.model.dto.request.TagRequestDto;
import com.vadim.springtask.model.dto.response.PageResponseDto;
import com.vadim.springtask.model.dto.response.TagResponseDto;
import com.vadim.springtask.model.entity.Tag;
import com.vadim.springtask.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.vadim.springtask.util.constants.TagConstants.TAG_ALREADY_EXISTS_WITH_NAME;
import static com.vadim.springtask.util.constants.TagConstants.TAG_NOT_FOUND_BY_ID;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagDao dao;
    private final TagMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public TagResponseDto getById(UUID id) {
        Tag tag = dao.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(TAG_NOT_FOUND_BY_ID, id))
        );
        return mapper.toResponseDto(tag);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<TagResponseDto> getAll(Integer page, Integer pageSize) {
        List<TagResponseDto> tagResponseDtos = dao.findAll(page, pageSize).stream()
                .map(mapper::toResponseDto)
                .toList();
        return PageResponseDto.<TagResponseDto>builder()
                .size(pageSize)
                .pageNumber(page)
                .elementsAmount(tagResponseDtos.size())
                .content(tagResponseDtos)
                .build();
    }

    @Override
    @Transactional
    public TagResponseDto save(TagRequestDto requestDto) {
        if (dao.existsByName(requestDto.getName())) {
            throw new DuplicateRecordException(String.format(TAG_ALREADY_EXISTS_WITH_NAME, requestDto.getName()));
        }
        return mapper.toResponseDto(dao.save(mapper.toEntity(requestDto)));
    }

    @Override
    @Transactional
    public TagResponseDto update(UUID id, TagRequestDto requestDto) {
        if (dao.existsByName(requestDto.getName())) {
            throw new DuplicateRecordException(String.format(TAG_ALREADY_EXISTS_WITH_NAME, requestDto.getName()));
        }
        Tag tag = dao.findById(id).orElseThrow(() ->
                new NotFoundException(String.format(TAG_NOT_FOUND_BY_ID, id))
        );
        mapper.updateTagFromDto(requestDto, tag);
        return mapper.toResponseDto(tag);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        if (!dao.existsById(id)) {
            throw new NotFoundException(String.format(TAG_NOT_FOUND_BY_ID, id));
        }
        dao.deleteById(id);
    }
}
