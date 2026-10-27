package io.hexlet.spring.shopping.mapper;

import io.hexlet.spring.shopping.model.BaseEntity;
import jakarta.persistence.EntityManager;
import org.mapstruct.Mapper;
import org.mapstruct.TargetType;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ReferenceMapper {

    @Autowired
    protected EntityManager entityManager;

    public <T extends BaseEntity> T toEntity(Long id, @TargetType Class<T> entityClass) {
        if (id == null) {
            return null;
        }
        T entity = entityManager.find(entityClass, id);
        if (entity == null) {
            throw new io.hexlet.spring.shopping.exception.ResourceNotFoundException(entityClass.getSimpleName(), id);
        }
        return entity;
    }
}
