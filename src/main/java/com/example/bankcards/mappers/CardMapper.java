package com.example.bankcards.mappers;

import com.example.bankcards.dto.CardDto;
import com.example.bankcards.dto.CardWithUserIdDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring",uses = CardMapperHelper.class)
public interface CardMapper {

    Card map (CardDto cardDto);
    CardDto mapToCardDto (Card card);
    @Mapping(target = "user", source = "userId")
    Card map (CardWithUserIdDto cardWithUserIdDto);
    @Mapping(target = "userId", source = "user")
    CardWithUserIdDto map (Card card);

}
