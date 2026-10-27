package io.hexlet.spring.shopping.mapper;

import io.hexlet.spring.shopping.dto.ShoppingListCreateDTO;
import io.hexlet.spring.shopping.dto.ShoppingListDTO;
import io.hexlet.spring.shopping.dto.ShoppingListUpdateDTO;
import io.hexlet.spring.shopping.model.ShoppingList;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {JsonNullableMapper.class, ReferenceMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ShoppingListMapper {

    @Mapping(target = "author", source = "authorId")
    public abstract ShoppingList map(ShoppingListCreateDTO dto);

    @Mapping(source = "author.id", target = "authorId")
    public abstract ShoppingListDTO map(ShoppingList model);

    @Mapping(target = "author", source = "authorId")
    public abstract void update(ShoppingListUpdateDTO dto, @MappingTarget ShoppingList model);
}
