package com.oviktor.mapper;

import com.oviktor.dto.CreateUserDto;
import com.oviktor.dto.DoctorDto;
import com.oviktor.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "role", expression = "java(com.oviktor.enums.Roles.DOCTOR)")
    @Mapping(target = "isTreated", constant = "false")
    User mapDoctorDtoToUser(CreateUserDto from);

    @Mapping(target = "role", expression = "java(com.oviktor.enums.Roles.NURSE)")
    @Mapping(target = "isTreated", constant = "false")
    User mapNurseDtoToUser(CreateUserDto from);

    @Mapping(target = "numOfPatients", constant = "0")
    DoctorDto mapUserToDoctorDto(User from);

    @Mapping(target = "role", expression = "java(com.oviktor.enums.Roles.PATIENT)")
    @Mapping(target = "isTreated", constant = "true")
    User mapPatientDtoToUser(CreateUserDto from);
}
