package com.vadim.springcore.integration.dao;

import com.vadim.springcore.config.TestConfig;
import com.vadim.springcore.dao.TagDao;
import com.vadim.springcore.factory.entity.TagFactory;
import com.vadim.springcore.model.entity.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.vadim.springcore.util.constants.TagTestConstants.ID;
import static org.junit.jupiter.api.Assertions.*;


// active profile
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfig.class})
@WebAppConfiguration
@Transactional
public class TagDaoTest {

    @Autowired
    private TagDao dao;

    private final int numberOfTags = 4;

    @Test
    void saveTestWithNotExistingTag() {
        Tag tag = TagFactory.getTagFactory().getInstance();
        Tag savedTag = dao.save(tag);
        tag.setId(savedTag.getId());

        assertEquals(tag, savedTag);
        assertEquals(numberOfTags + 1, dao.findAll().size());
    }

    @Test
    void saveIfNotExistsTestWithNotExistingTagByName() {
        Tag tag = TagFactory.getTagFactory().getInstance();
        tag.setName("a new name");

        Tag savedTag = dao.saveIfNotExists(tag);
        tag.setId(savedTag.getId());

        assertEquals(tag, savedTag);
        assertEquals(numberOfTags + 1, dao.findAll().size());
    }

    @Test
    void saveIfNotExistsTestWithExistingTagByName() {
        Tag tag = TagFactory.getTagFactory().getInstance();
        tag.setName("name1");

        Tag savedTag = dao.saveIfNotExists(tag);
        tag.setId(savedTag.getId());

        assertEquals(tag, savedTag);
        assertEquals(numberOfTags, dao.findAll().size());
    }

    @Test
    void findAllByGiftCertificateIdTest() {
        UUID giftCertificateID = UUID.fromString("7120a2ad-215a-4c3b-9f5e-f80234c15eba");

        List<Tag> tags = dao.findAllByGiftCertificateId(giftCertificateID);

        assertEquals(2, tags.size());
    }

    @Test
    void existsByIdTestWithExistingId() {
        Tag tag = dao.findAll().get(0);

        assertTrue(dao.existsById(ID));
    }

    @Test
    void existsByIdTestWithNotExistingId() {
        UUID id = UUID.randomUUID();

        assertFalse(dao.existsById(id));
    }

    @Test
    void existsByNameTestWithNotExistingName() {
        String name = "random name";

        assertFalse(dao.existsByName(name));
    }

    @Test
    void existsByNameTestWithExistingName() {
        String name = dao.findAll().get(0).getName();

        assertTrue(dao.existsByName(name));
    }

    @Test
    void findAllTest() {
        assertEquals(numberOfTags, dao.findAll().size());
    }

    @Test
    void findByIdTestWithExistingId() {
        Tag tag = dao.findAll().get(0);
        UUID id = tag.getId();

        Optional<Tag> optionalTag = dao.findById(id);

        assertTrue(optionalTag.isPresent());
        assertEquals(tag, optionalTag.get());
    }

    @Test
    void findByIdTestWithNotExistingId() {
        UUID id = UUID.randomUUID();

        Optional<Tag> optionalTag = dao.findById(id);

        assertFalse(optionalTag.isPresent());
    }

    @Test
    void deleteById() {
        UUID id = UUID.fromString("78512969-138e-4a2c-96ff-34b0e302ed08");

        dao.deleteById(id);

        Optional<Tag> optionalTag = dao.findById(id);

        assertFalse(optionalTag.isPresent());
        assertEquals(numberOfTags - 1, dao.findAll().size());
    }

    @Test
    void updateTest() {
        Tag tag = dao.findAll().get(0);
        UUID id = tag.getId();
        tag.setName("a new name");

        dao.update(tag);

        Optional<Tag> optionalTag = dao.findById(id);

        assertTrue(optionalTag.isPresent());
        assertEquals(tag, optionalTag.get());
    }
}