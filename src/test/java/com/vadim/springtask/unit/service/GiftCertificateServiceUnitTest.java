package com.vadim.springtask.unit.service;

import com.vadim.springtask.dao.GiftCertificateDao;
import com.vadim.springtask.dao.GiftCertificateTagDao;
import com.vadim.springtask.dao.TagDao;
import com.vadim.springtask.exception.NotFoundException;
import com.vadim.springtask.factory.dto.request.GiftCertificateRequestDtoFactory;
import com.vadim.springtask.factory.dto.response.GiftCertificateResponseDtoFactory;
import com.vadim.springtask.factory.entity.GiftCertificateFactory;
import com.vadim.springtask.factory.entity.TagFactory;
import com.vadim.springtask.model.dto.mapper.GiftCertificateMapper;
import com.vadim.springtask.model.dto.request.GiftCertificateRequestDto;
import com.vadim.springtask.model.dto.response.GiftCertificateResponseDto;
import com.vadim.springtask.model.entity.GiftCertificate;
import com.vadim.springtask.model.entity.GiftCertificateTag;
import com.vadim.springtask.model.entity.GiftCertificateTagId;
import com.vadim.springtask.model.entity.Tag;
import com.vadim.springtask.service.impl.GiftCertificateServiceImpl;
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

import static com.vadim.springtask.util.constants.GiftCertificateTestConstants.ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Gift certificate unit tests")
@ExtendWith(MockitoExtension.class)
public class GiftCertificateServiceUnitTest {

    @Mock
    private GiftCertificateTagDao giftCertificateTagDao;

    @Mock
    private TagDao tagDao;

    @Mock
    private GiftCertificateDao dao;
    @Mock
    private GiftCertificateMapper mapper;

    @InjectMocks
    private GiftCertificateServiceImpl service;
    private GiftCertificate giftCertificate;
    private GiftCertificateResponseDto responseDto;
    private GiftCertificateRequestDto requestDto;
    private UUID id;

    @BeforeEach
    void setUp() {
        id = ID;
        giftCertificate = GiftCertificateFactory.getTagFactory().getInstance();
        requestDto = GiftCertificateRequestDtoFactory.getTagFactory().getInstance();
        responseDto = GiftCertificateResponseDtoFactory.getTagFactory().getInstance();
    }

    @Nested
    @DisplayName("Getting gift certificates test")
    class GetGiftCertificateTest {
        @Test
        @DisplayName("Get gift certificate by id")
        void Given_GiftCertificateId_When_CertificateWithIdExists_Then_CertificateIsReturned() {
            when(dao.findById(id)).thenReturn(Optional.of(giftCertificate));
            when(mapper.toResponseDto(giftCertificate)).thenReturn(responseDto);

            assertEquals(responseDto, service.getById(id));

            verify(dao, only()).findById(id);
            verify(mapper, only()).toResponseDto(giftCertificate);
        }

        @Test
        @DisplayName("Get gift certificate by id that is not found")
        void Given_TagId_WhenTagIsNotFound_Then_ThrowsNotFoundException() {
            when(dao.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.getById(id));

            verify(dao, only()).findById(id);
            verifyNoInteractions(mapper);
        }

        @Test
        @DisplayName("Get all gift certificates")
        void Given_Nothing_When_AllGiftCertificatesAreRequested_Then_AllGiftCertificatesAreReturned() {
            int listSize = 3;
            List<GiftCertificate> giftCertificates = GiftCertificateFactory.getTagFactory().getInstanceList(listSize);
            List<GiftCertificateResponseDto> responseDtos = GiftCertificateResponseDtoFactory.getTagFactory().getInstanceList(listSize);

            when(dao.findAll(anyInt(),anyInt())).thenReturn(giftCertificates);
            when(mapper.toResponseDto(any())).thenReturn(responseDto);

            assertEquals(responseDtos, service.getAll(anyInt(), anyInt()));

            verify(dao, only()).findAll();
            verify(mapper, times(listSize)).toResponseDto(giftCertificate);
            verifyNoMoreInteractions(mapper);
        }
    }


    @Nested
    @DisplayName("Saving gift certificates tests")
    class SaveTagTest {
        @Test
        @DisplayName("Save gift certificate")
        void Given_GiftCertificateRequestDto_When_SavingGiftCertificate_Then_SavedGiftCertificateIsReturned() {
            Tag tag = TagFactory.getTagFactory().getInstance();
            GiftCertificateTag giftCertificateTag = new GiftCertificateTag(new GiftCertificateTagId(id, tag.getId()));

            when(mapper.toEntity(requestDto)).thenReturn(giftCertificate);
            when(dao.save(any())).thenReturn(giftCertificate);
            when(tagDao.save(any())).thenReturn(tag);
            when(giftCertificateTagDao.save(any())).thenReturn(giftCertificateTag);
            when(dao.save(giftCertificate)).thenReturn(giftCertificate);
            when(mapper.toResponseDto(any())).thenReturn(responseDto);

            assertEquals(responseDto, service.save(requestDto));

            verify(dao).save(any());
            verify(tagDao, times(3)).save(any());
            verify(giftCertificateTagDao, times(3)).save(any());
            verify(mapper, times(1)).toEntity(requestDto);
            verify(mapper, times(1)).toResponseDto(giftCertificate);
            verifyNoMoreInteractions(dao, tagDao, giftCertificateTagDao, mapper);
        }
    }

    @Nested
    @DisplayName("Updating gift certificates tests")
    class UpdateTagTest {
        @Test
        @DisplayName("Update gift certificate")
        void Given_GiftCertificateRequestDto_When_UpdatingGiftCertificate_Then_UpdatedGiftCertificateIsReturned() {
            Tag tag = giftCertificate.getTags().get(0);
            GiftCertificateTag giftCertificateTag = new GiftCertificateTag(new GiftCertificateTagId(id, tag.getId()));


            when(dao.findById(id)).thenReturn(Optional.of(giftCertificate));
            doNothing().when(mapper).updateGiftCertificateFromDto(requestDto, giftCertificate);
            when(dao.update(giftCertificate)).thenReturn(giftCertificate);
            when(tagDao.save(tag)).thenReturn(tag);
            when(giftCertificateTagDao.save(any())).thenReturn(giftCertificateTag);
            when(tagDao.findAllByGiftCertificateId(id)).thenReturn(giftCertificate.getTags());
            when(mapper.toResponseDto(any())).thenReturn(responseDto);

            assertEquals(responseDto, service.update(id, requestDto));

            verify(dao, times(1)).findById(id);
            verify(mapper, times(1)).updateGiftCertificateFromDto(requestDto, giftCertificate);
            verify(dao, times(1)).update(giftCertificate);
            verify(tagDao, times(3)).save(tag);
            verify(giftCertificateTagDao, times(3)).save(any());
            verify(tagDao, times(1)).findAllByGiftCertificateId(id);
            verify(mapper, times(1)).toResponseDto(giftCertificate);
        }


        @Test
        @DisplayName("Update gift certificate that is not found by id")
        void Given_GiftCertificateRequestDto_When_CertificateIsNotFoundById_Then_NotFoundExceptionIsThrown() {
            when(dao.findById(id)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class, () -> service.update(id, requestDto));

            verify(dao, only()).findById(id);
            verifyNoInteractions(mapper);
        }
    }

    @Nested
    @DisplayName("Deleting gift certificates tests")
    class DeleteGiftCertificateTest {
        @Test
        @DisplayName("Delete gift certificate by id")
        void Given_GiftCertificateId_When_DeletingExistingCertificate_Then_CertificateIsDeleted() {
            when(dao.existsById(id)).thenReturn(true);
            doNothing().when(dao).deleteById(id);
            doNothing().when(giftCertificateTagDao).deleteByGiftCertificateId(id);

            service.deleteById(id);

            verify(dao, times(1)).existsById(id);
            verify(dao, times(1)).deleteById(id);
            verify(giftCertificateTagDao, only()).deleteByGiftCertificateId(id);
        }

        @Test
        @DisplayName("Delete gift certificate that id not found by id")
        void Given_GiftCertificateId_When_DeletingNotExistingCertificate_Then_NotFoundExceptionIsThrown() {
            when(dao.existsById(id)).thenReturn(false);

            assertThrows(NotFoundException.class, () -> service.deleteById(id));

            verify(dao, only()).existsById(id);
            verifyNoInteractions(giftCertificateTagDao);
            verifyNoMoreInteractions(dao);
        }
    }
}