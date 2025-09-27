package com.example.bankcards.mappers;

import com.example.bankcards.entity.Card;
import generated.com.example.bankcards.api.model.CardDto;
import generated.com.example.bankcards.api.model.CardWithUserIdDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = CardMapperHelper.class)
public interface CardMapper {

    Card map (CardDto cardDto);
    CardDto mapToCardDto (Card card);
    @Mapping(target = "user", source = "userId")
    Card map (CardWithUserIdDto cardWithUserIdDto);
    CardWithUserIdDto map (Card card);

}
