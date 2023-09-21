package fittering.mall.domain.mapper;

import fittering.mall.controller.dto.request.RequestUserDto;
import fittering.mall.controller.dto.response.ResponseUserDto;
import fittering.mall.service.dto.UserDto;
import fittering.mall.controller.dto.request.RequestLoginDto;
import fittering.mall.repository.dto.SavedUserDto;
import fittering.mall.service.dto.SignUpDto;
import fittering.mall.service.dto.LoginDto;
import fittering.mall.controller.dto.request.RequestSignUpDto;
import fittering.mall.domain.entity.Measurement;
import fittering.mall.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    LoginDto toLoginDto(RequestLoginDto requestLoginDto);
    SignUpDto toSignUpDto(RequestSignUpDto requestSignUpDto);
    @Mappings({
            @Mapping(source = "measurement.id", target = "id", ignore = true),
            @Mapping(source = "password", target = "password")
    })
    User toUser(SignUpDto signUpDto, Integer ageRange, String password, Measurement measurement, List<String> roles);
    UserDto toUserDto(SavedUserDto savedUserDto);
    UserDto toUserDto(RequestUserDto requestUserDto);
    ResponseUserDto toResponseUserDto(UserDto userDto);
}
