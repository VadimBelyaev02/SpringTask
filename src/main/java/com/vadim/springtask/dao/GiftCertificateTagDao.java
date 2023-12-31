package com.vadim.springtask.dao;

import com.vadim.springtask.model.entity.GiftCertificateTag;
import com.vadim.springtask.model.entity.GiftCertificateTagId;

import java.util.UUID;

public interface GiftCertificateTagDao extends CrudDao<GiftCertificateTag, GiftCertificateTagId> {

    void deleteByGiftCertificateId(UUID id);

    void deleteByTagId(UUID id);

    boolean existsById(GiftCertificateTagId id);
}
