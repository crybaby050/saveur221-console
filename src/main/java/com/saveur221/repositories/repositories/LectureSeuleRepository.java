package com.saveur221.repositories;

import java.util.List;
import java.util.Optional;

public interface LectureSeuleRepository<T, ID> {

    // Même contrat de lecture que Repository<T, ID>, mais sans save/update/
    // deleteById : la création et la modification des clients restent
    // entièrement portées par le module PHP Web.
    Optional<T> findById(ID id);

    List<T> findAll();
}