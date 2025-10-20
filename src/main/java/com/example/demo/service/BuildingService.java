package com.example.demo.service;

import com.example.demo.model.dto.AssignmentDTO;
import com.example.demo.model.dto.BuildingDTO;
import com.example.demo.model.response.BuildingSearchResponse;
import com.example.demo.model.response.ResponseDTO;
import java.util.List;
import java.util.Map;

public interface BuildingService {
    List <BuildingSearchResponse> findAll(Map<String, Object> conditions, List<String> typeCode);
    ResponseDTO save(BuildingDTO buildingDTO);
    BuildingDTO findBuildingById(Long id);
    ResponseDTO deleteBuildings(List<Long> buildingIds);
    ResponseDTO findStaffsByBuildingId(Long buildingId);
    ResponseDTO updateAssignmentTable(AssignmentDTO assignmentBuildingDTO);
}
