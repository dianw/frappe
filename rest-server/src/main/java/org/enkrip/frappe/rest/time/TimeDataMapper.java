package org.enkrip.frappe.rest.time;

import org.enkrip.frappe.metadata.time.GetCurrentTimeResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TimeDataMapper {
    TimeDataMapper INSTANCE = Mappers.getMapper(TimeDataMapper.class);

    @Mapping(target = "currentTimeISO", source = "currentTimeIso")
    GetCurrentTimeResponseData toGetCurrentTimeResponseData(GetCurrentTimeResponse response);
}
