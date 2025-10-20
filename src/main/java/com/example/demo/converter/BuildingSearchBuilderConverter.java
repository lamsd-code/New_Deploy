package com.example.demo.converter;


import com.example.demo.builder.BuildingSearchBuilder;
import com.example.demo.utils.MapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class BuildingSearchBuilderConverter {
    public BuildingSearchBuilder toBuildingSearchBuilder(Map<String, Object> params, List<String> typeCode){
        BuildingSearchBuilder buildingSearchBuilder = new BuildingSearchBuilder.Builder()
                .setName(MapUtils.getObject(params, "name", String.class))
                .setFloorArea(MapUtils.getObject(params, "floorArea", Integer.class))
                .setNumberOfBasement(MapUtils.getObject(params, "numberOfBasement", Integer.class))
                .setWard(MapUtils.getObject(params, "ward", String.class))
                .setDistrict(MapUtils.getObject(params, "district", String.class))
                .setStreet(MapUtils.getObject(params, "street", String.class))
                .setManagerName(MapUtils.getObject(params, "managerName", String.class))
                .setManagerPhone(MapUtils.getObject(params, "managerPhone", String.class))
                .setDirection(MapUtils.getObject(params, "direction", String.class))
                .setLevel(MapUtils.getObject(params, "level", String.class))
                .setRentPriceFrom(MapUtils.getObject(params, "rentPriceFrom", Integer.class))
                .setRentPriceTo(MapUtils.getObject(params, "rentPriceTo", Integer.class))
                .setAreaFrom(MapUtils.getObject(params, "areaFrom", Integer.class))
                .setAreaTo(MapUtils.getObject(params, "areaTo", Integer.class))
                .setStaffId(MapUtils.getObject(params, "staffId", Integer.class))
                .setTypeCode(typeCode)
                .build();
        return buildingSearchBuilder;
    }
}
