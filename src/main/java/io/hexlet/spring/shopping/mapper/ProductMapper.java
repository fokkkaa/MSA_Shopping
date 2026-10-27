package io.hexlet.spring.shopping.mapper;

import io.hexlet.spring.shopping.dto.ProductCreateDTO;
import io.hexlet.spring.shopping.dto.ProductDTO;
import io.hexlet.spring.shopping.dto.ProductUpdateDTO;
import io.hexlet.spring.shopping.model.Product;
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
public abstract class ProductMapper {

    @Mapping(target = "list", source = "listId")
    @Mapping(target = "author", source = "authorId")
    public abstract Product map(ProductCreateDTO dto);

    @Mapping(source = "list.id", target = "listId")
    @Mapping(source = "author.id", target = "authorId")
    public abstract ProductDTO map(Product model);

    @Mapping(target = "list", source = "listId")
    @Mapping(target = "author", source = "authorId")
    public abstract void update(ProductUpdateDTO dto, @MappingTarget Product model);
}
