package com.library.manager.repositories.mappers;

import com.library.manager.domain.Editorial;
import com.library.manager.repositories.models.EditorialEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {BookEntityMapper.class, PaginationMapper.class})
public interface EditorialEntityMapper {

    Editorial toDomain(EditorialEntity editorialEntity);

    EditorialEntity toEntity(Editorial editorial);

}
