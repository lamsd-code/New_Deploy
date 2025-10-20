package com.example.demo.repository.custom;


import com.example.demo.builder.BuildingSearchBuilder;
import com.example.demo.entity.Building;
import com.example.demo.model.dto.BuildingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BuildingRepositoryCustom {
    List<Building> findAll(BuildingSearchBuilder buildingSearchBuilder);
//    List<BuildingEntity> getAllBuildings(Pageable pageable);
//
//    int countTotalItem();
}

