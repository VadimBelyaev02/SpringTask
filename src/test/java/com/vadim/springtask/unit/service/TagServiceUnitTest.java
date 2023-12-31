package com.vadim.springtask.unit.service;

import com.vadim.springtask.dao.GiftCertificateTagDao;
import com.vadim.springtask.dao.TagDao;
import com.vadim.springtask.exception.DuplicateRecordException;
import com.vadim.springtask.exception.NotFoundException;
import com.vadim.springtask.factory.dto.request.TagRequestDtoFactory;
import com.vadim.springtask.factory.dto.response.TagResponseDtoFactory;
import com.vadim.springtask.factory.entity.TagFactory;
import com.vadim.springtask.model.dto.mapper.TagMapper;
import com.vadim.springtask.model.dto.request.TagRequestDto;
import com.vadim.springtask.model.dto.response.TagResponseDto;
import com.vadim.springtask.model.entity.Tag;
import com.vadim.springtask.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.vadim.springtask.util.constants.TagTestConstants.ID;
import static com.vadim.springtask.util.constants.TagTestConstants.NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Tag unit tests")
@ExtendWith(MockitoExtension.class)
public class TagServiceUnitTest {
    @Mock
    private GiftCertificateTagDao giftCertificateTagDao;

    @Mock
    private TagDao tagDao;

    @Mock
    private TagMapper mapper;

    @InjectMocks
    private TagServiceImpl service;
    private Tag tag;
    private TagResponseDto responseDto;
    private TagRequestDto requestDto;
    private UUID id;
    private String name;

    @BeforeEach
    void setUp() {
        id = ID;
        name = NAME;
        tag = TagFactory.getTagFactory().getInstance();
        requestDto = TagRequestDtoFactory.getTagFactory().getInstance();
        responseDto = TagResponseDtoFactory.getTagFactory().getInstance();
    }

    @Nested
    @DisplayName("Getting tags test")
    class GetTagTest {
        @Test
        @DisplayName("Get tag by id")
        void Given_TagId_When_TagWithIdExists_Then_TagIsReturned() {
            when(tagDao.findById(id)).thenReturn(Optional.of(tag));
            when(mapper.toResponseDto(tag)).thenReturn(responseDto);

            assertEquals(responseDto, service.getById(id));

            verify(tagDao, only()).findById(id);
            verify(mapper, only()).toResponseDto(tag);
        }

        @Test
        @DisplayName("Get tag by id that is not found")
        void Given_TagId_WhenTagIsNotFound_Then_ThrowsNotFoundException() {
            when(tagDao.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.getById(id));

            verify(tagDao, only()).findById(id);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Get all tags")
        void Given_Nothing_When_AllTagsRequested_Then_AllTagsAreReturned() {
            int listSize = 3;
            List<Tag> tags = TagFactory.getTagFactory().getInstanceList();
            List<TagResponseDto> tagResponseDtos = TagResponseDtoFactory.getTagFactory().getInstanceList(listSize);

            when(tagDao.findAll(anyInt(), anyInt())).thenReturn(tags);
            when(mapper.toResponseDto(any())).thenReturn(responseDto);

            assertEquals(tagResponseDtos, service.getAll(anyInt(), anyInt()));

            verify(tagDao, only()).findAll();
            verify(mapper, times(listSize)).toResponseDto(tag);
            verifyNoMoreInteractions(mapper);
        }
    }


    @Nested
    @DisplayName("Saving tags tests")
    class SaveTagTest {
        @Test
        @DisplayName("Save tag")
        void Given_TagRequestDto_When_SavingTag_Then_SavedTagIsReturned() {
            when(tagDao.existsByName(name)).thenReturn(false);
            when(mapper.toEntity(requestDto)).thenReturn(tag);
            when(mapper.toResponseDto(tag)).thenReturn(responseDto);
            when(tagDao.save(tag)).thenReturn(tag);

            assertEquals(responseDto, service.save(requestDto));

            verify(tagDao, times(1)).save(tag);
            verify(tagDao, times(1)).existsByName(name);
            verify(mapper, times(1)).toResponseDto(tag);
            verify(mapper, times(1)).toEntity(requestDto);
            verifyNoMoreInteractions(mapper, tagDao);
        }

        @Test
        @DisplayName("Save tag that already exists by name")
        void Given_TagRequestDto_When_SavingTagWithExistingName_Then_DuplicateRecordExceptionIsThrown() {
            when(tagDao.existsByName(name)).thenReturn(true);

            assertThrows(DuplicateRecordException.class, () -> service.save(requestDto));

            verify(tagDao, only()).existsByName(any());
            verifyNoInteractions(mapper);
        }
    }

    @Nested
    @DisplayName("Updating tags tests")
    class UpdateTagTest {
        @Test
        @DisplayName("Update tag")
        void Given_TagRequestDto_When_UpdatingTag_Then_UpdatedTagIsReturned() {
            when(tagDao.update(tag)).thenReturn(tag);
            when(tagDao.existsByName(name)).thenReturn(false);
            when(tagDao.findById(id)).thenReturn(Optional.of(tag));
            when(mapper.toResponseDto(tag)).thenReturn(responseDto);
            doNothing().when(mapper).updateTagFromDto(requestDto, tag);

            assertEquals(responseDto, service.update(id, requestDto));

            verify(tagDao, times(1)).update(tag);
            verify(tagDao, times(1)).existsByName(name);
            verify(tagDao, times(1)).findById(id);
            verify(mapper, times(1)).updateTagFromDto(requestDto, tag);
            verify(mapper, times(1)).toResponseDto(tag);
            verifyNoMoreInteractions(mapper, tagDao);
        }

        @Test
        @DisplayName("Update tag that already exists by name")
        void Given_TagRequestDto_When_UpdatingTagWithExistedName_Then_DuplicateRecordExceptionIsThrown() {
            when(tagDao.existsByName(name)).thenReturn(true);

            assertThrows(DuplicateRecordException.class, () -> service.update(id, requestDto));

            verify(tagDao, only()).existsByName(name);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Update tag that it not found by id")
        void Given_TagRequestDto_When_UpdatingTagThatIsNotFoundById_Then_NotFoundExceptionIsThrown() {
            when(tagDao.existsByName(name)).thenReturn(false);
            when(tagDao.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.update(id, requestDto));

            verify(tagDao, times(1)).existsByName(name);
            verify(tagDao, times(1)).findById(id);
            verifyNoMoreInteractions(tagDao, mapper);
        }
    }

    @Nested
    @DisplayName("Deleting tags tests")
    class DeleteTagTest {
        @Test
        @DisplayName("Delete tag by id")
        void Given_TagId_When_DeletingExistingTag_Then_TagIsDeleted() {
            when(tagDao.existsById(id)).thenReturn(true);
            doNothing().when(tagDao).deleteById(id);
            doNothing().when(giftCertificateTagDao).deleteByTagId(id);

            service.deleteById(id);

            verify(tagDao, times(1)).existsById(id);
            verify(tagDao, times(1)).deleteById(id);
            verify(giftCertificateTagDao, only()).deleteByTagId(id);
        }

        @Test
        @DisplayName("Delete tag that id not found by id")
        void Given_TagId_When_DeletingNotExistingTag_Then_NotFoundExceptionIsThrown() {
            when(tagDao.existsById(id)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> service.deleteById(id));

            verify(tagDao, only()).existsById(id);
            verifyNoInteractions(giftCertificateTagDao);
            verifyNoMoreInteractions(tagDao);
        }
    }
}
