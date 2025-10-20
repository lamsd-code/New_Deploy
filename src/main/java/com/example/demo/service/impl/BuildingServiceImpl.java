package com.example.demo.service.impl;

import com.example.demo.builder.BuildingSearchBuilder;
import com.example.demo.converter.BuildingDTOConverter;
import com.example.demo.converter.BuildingSearchBuilderConverter;
import com.example.demo.entity.Building;
import com.example.demo.entity.User;
import com.example.demo.model.dto.AssignmentDTO;
import com.example.demo.model.dto.BuildingDTO;
import com.example.demo.model.response.BuildingSearchResponse;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.model.response.StaffResponseDTO;
import com.example.demo.repository.BuildingRepository;
import com.example.demo.repository.RentAreaRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BuildingServiceImpl implements BuildingService {
    @Autowired
    private BuildingRepository buildingRepository;
    @Autowired
    private BuildingDTOConverter buildingDTOConverter;
    @Autowired
    private BuildingSearchBuilderConverter buildingSearchBuilderConverter;
    @Autowired
    private RentAreaRepository rentAreaRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List <BuildingSearchResponse> findAll(Map<String, Object> params, List<String> typeCode){
        BuildingSearchBuilder buildingSearchBuilder = buildingSearchBuilderConverter.toBuildingSearchBuilder(params, typeCode);
        List < Building> buildingEntities;
     // ✅ Nếu có staffId → chỉ load tòa nhà của nhân viên đó
        if (params.containsKey("staffId")) {
            Long staffId = Long.parseLong(params.get("staffId").toString());
            buildingEntities = buildingRepository.findByStaffId(staffId);
        } else {
            buildingEntities = buildingRepository.findAll(buildingSearchBuilder);
        }
        List<BuildingSearchResponse> result = new ArrayList<>();
        for(Building b : buildingEntities){
            result.add(buildingDTOConverter.toBuildingRespone(b));
        }
        return result;
    }

    @Override
    public ResponseDTO save(BuildingDTO buildingDTO){
        ResponseDTO responseDTO = new ResponseDTO();
        Building buildingEntity = buildingDTOConverter.toBuildingEntity(buildingDTO);
        List<Building> buildingEntities = new ArrayList<>();
        buildingEntities.add(buildingEntity);
        List<User> userEntities = new ArrayList<>();
        if(buildingEntity.getId() != null){
            userEntities = userRepository.findByBuildingEntityList(buildingEntities);
            responseDTO.setMessage("Cập nhật tòa nhà thành công");
        }else responseDTO.setMessage("Thêm tòa nhà thành công");
        buildingEntity = buildingRepository.save(buildingEntity);
        buildingEntity.setUserEntities(userEntities);
        return responseDTO;
    }

    @Override
    public BuildingDTO findBuildingById(Long id) {
        Building buildingEntity = buildingRepository.findById(id).get();
        return buildingDTOConverter.toBuildingDTO(buildingEntity);
    }

    @Override
    public ResponseDTO deleteBuildings(List<Long> buildingIds) {
        rentAreaRepository.deleteBybuilding_IdIn(buildingIds);
        buildingRepository.deleteAllByIdIn(buildingIds);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("success");
        return responseDTO;
    }

    @Override
    public ResponseDTO findStaffsByBuildingId(Long buildingId) {
        Building buildingEntity = buildingRepository.findById(buildingId).get();
        List<User> staffList = userRepository.findByStatusAndRoles_Code(1, "STAFF");
        List<User> assignedStaffList = buildingEntity.getUserEntities();
        List<StaffResponseDTO> staffResponseDTOS = new ArrayList<>();
        for(User u : staffList){
            StaffResponseDTO staffResponseDTO = new StaffResponseDTO();
            staffResponseDTO.setStaffId(u.getId());
            staffResponseDTO.setFullName(u.getFullName());
            if(assignedStaffList.contains(u)){
                staffResponseDTO.setChecked("checked");
            }
            else staffResponseDTO.setChecked("");
            staffResponseDTOS.add(staffResponseDTO);
        }
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setData(staffResponseDTOS);
        responseDTO.setMessage("success");
        return responseDTO;
    }

    @Override
    public ResponseDTO updateAssignmentTable(AssignmentDTO assignmentBuildingDTO) {
        List<Long> staffIds = assignmentBuildingDTO.getStaffs();
        Building buildingEntity = buildingRepository.findById(assignmentBuildingDTO.getId()).get();
        List<User> userEntities = new ArrayList<>();
        for(Long id : staffIds){
            userEntities.add(userRepository.findById(id).get());
        }
        buildingEntity.setUserEntities(userEntities);
        ResponseDTO responseDTO = new ResponseDTO();
        responseDTO.setMessage("success");
        return responseDTO;
    }
}
