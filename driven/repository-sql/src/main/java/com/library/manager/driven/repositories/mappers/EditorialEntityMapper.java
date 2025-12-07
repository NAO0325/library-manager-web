package com.library.manager.driven.repositories.mappers;

import com.library.manager.domain.Editorial;
import com.library.manager.driven.repositories.models.EditorialEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EditorialEntityMapper {

    Editorial toDomain(EditorialEntity editorialEntity);

    EditorialEntity toEntity(Editorial editorial);

}
