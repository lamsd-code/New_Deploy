package com.example.demo.converter;


import com.example.demo.entity.Building;
import com.example.demo.entity.RentArea;
import com.example.demo.model.dto.BuildingDTO;
import com.example.demo.model.response.BuildingSearchResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
@Component
public class BuildingDTOConverter {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired RentAreaConverter rentAreaConverter;
    public BuildingDTO toBuildingDTO(Building item){
        BuildingDTO building = modelMapper.map(item, BuildingDTO.class);
        building.setAddress(item.getStreet() + ", " + item.getWard() + ", " + item.getDistrict());
        List<RentArea> rentAreas = item.getRentAreaEntities();
        List<String> typeCodes = new ArrayList<>();
        if(item.getTypeCode() != null){
            String[] typeCode = item.getTypeCode().split(",");
            for(String type : typeCode){
                typeCodes.add(type);
            }
        }
        building.setTypeCode(typeCodes);
        if(rentAreas != null){
            String rentArea = rentAreas.stream().map(it->it.getValue().toString()).collect(Collectors.joining(", "));
            building.setRentArea(rentArea);
        }
        return building;
    }
    public BuildingSearchResponse toBuildingRespone(Building item){
        BuildingSearchResponse building = modelMapper.map(item, BuildingSearchResponse.class);
        building.setAddress(item.getStreet() + ", " + item.getWard() + ", " + item.getDistrict());
        building.setImageUrl(item.getAvatar());
        List<RentArea> rentAreas = item.getRentAreaEntities();
        if(rentAreas != null){
            String rentArea = rentAreas.stream().map(it->it.getValue().toString()).collect(Collectors.joining(", "));
            building.setRentArea(rentArea);
        }
        return building;
    }
    public Building toBuildingEntity(BuildingDTO buildingDTO){
        Building buildingEntity = modelMapper.map(buildingDTO, Building.class);
        String type = buildingDTO.getTypeCode().stream().map(it->it.toString()).collect(Collectors.joining(","));
        buildingEntity.setTypeCode(type);
        buildingEntity.setRentAreaEntities(rentAreaConverter.listRentAreaEntity(buildingDTO, buildingEntity));
        return buildingEntity;
    }
    public BuildingSearchResponse toBuildingSearchResponse(BuildingDTO buildingDTO){
        BuildingSearchResponse buildingSearchResponse = new BuildingSearchResponse();
        buildingSearchResponse.setId(buildingDTO.getId());
        buildingSearchResponse.setName(buildingDTO.getName());
        buildingSearchResponse.setAddress(buildingDTO.getAddress());
        buildingSearchResponse.setManagerPhone(buildingDTO.getManagerPhone());
        buildingSearchResponse.setNumberOfBasement(buildingDTO.getNumberOfBasement());
        buildingSearchResponse.setManagerName(buildingDTO.getManagerName());
        buildingSearchResponse.setServiceFee(buildingDTO.getServiceFee());
        buildingSearchResponse.setFloorArea(buildingDTO.getFloorArea());
        buildingSearchResponse.setRentArea(buildingDTO.getRentArea());
        return buildingSearchResponse;
    }
}
