package fittering.mall.domain.mapper;

import fittering.mall.service.dto.MeasurementDto;
import fittering.mall.controller.dto.request.RequestMeasurementDto;
import fittering.mall.controller.dto.response.ResponseMeasurementDto;
import fittering.mall.repository.dto.SavedMeasurementDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MeasurementMapper {
    MeasurementMapper INSTANCE = Mappers.getMapper(MeasurementMapper.class);

    ResponseMeasurementDto toResponseMeasurementDto(MeasurementDto measurementDto);
    MeasurementDto toMeasurementDto(RequestMeasurementDto requestMeasurementDto);
    MeasurementDto toMeasurementDto(SavedMeasurementDto savedMeasurementDto);
}
