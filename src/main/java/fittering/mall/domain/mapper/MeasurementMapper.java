package fittering.mall.domain.mapper;

import fittering.mall.domain.dto.service.MeasurementDto;
import fittering.mall.domain.dto.controller.request.RequestMeasurementDto;
import fittering.mall.domain.dto.controller.response.ResponseMeasurementDto;
import fittering.mall.domain.dto.repository.SavedMeasurementDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeasurementMapper {
    MeasurementMapper INSTANCE = Mappers.getMapper(MeasurementMapper.class);

    ResponseMeasurementDto toResponseMeasurementDto(MeasurementDto measurementDto);
    MeasurementDto toMeasurementDto(RequestMeasurementDto requestMeasurementDto);
    MeasurementDto toMeasurementDto(SavedMeasurementDto savedMeasurementDto);
}
