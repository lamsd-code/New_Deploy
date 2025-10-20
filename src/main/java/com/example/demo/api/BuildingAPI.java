package com.example.demo.api;

import com.example.demo.model.dto.AssignmentDTO;
import com.example.demo.model.dto.BuildingDTO;
import com.example.demo.model.response.ResponseDTO;
import com.example.demo.service.BuildingService;
import com.example.demo.service.impl.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController(value = "buildingApiOfBuilding")
@Transactional
@RequestMapping("/api/building")
public class BuildingAPI {

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private CloudinaryService cloudinaryService;

    // ✅ 1️⃣ Thêm mới tòa nhà
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @PostMapping
    public ResponseEntity<ResponseDTO> addBuilding(
            @ModelAttribute BuildingDTO buildingDTO,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {

        try {
            // Upload ảnh lên Cloudinary nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String imageUrl = cloudinaryService.uploadFile(avatarFile, "buildings");
                buildingDTO.setAvatar(imageUrl);
            }

            ResponseDTO responseDTO = buildingService.save(buildingDTO);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO error = new ResponseDTO();
            error.setMessage("❌ Lỗi khi thêm tòa nhà: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ 2️⃣ Cập nhật tòa nhà
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDTO> updateBuilding(
            @PathVariable Long id,
            @ModelAttribute BuildingDTO buildingDTO,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {

        try {
            // Upload ảnh mới nếu có
            if (avatarFile != null && !avatarFile.isEmpty()) {
                String imageUrl = cloudinaryService.uploadFile(avatarFile, "buildings");
                buildingDTO.setAvatar(imageUrl);
            }

            buildingDTO.setId(id); // gán id cho DTO để update
            ResponseDTO responseDTO = buildingService.save(buildingDTO);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            e.printStackTrace();
            ResponseDTO error = new ResponseDTO();
            error.setMessage("❌ Lỗi khi cập nhật tòa nhà: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // ✅ 3️⃣ Xóa tòa nhà
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseDTO deleteBuilding(@PathVariable Long id) {
        return buildingService.deleteBuildings(List.of(id));
    }

    // ✅ 4️⃣ Lấy danh sách nhân viên được gán cho tòa nhà
    @PreAuthorize("hasAnyAuthority('STAFF','MANAGER','ADMIN')")
    @GetMapping("/{id}/staffs")
    public ResponseDTO loadStaffs(@PathVariable Long id) {
        return buildingService.findStaffsByBuildingId(id);
    }

    // ✅ 5️⃣ Cập nhật phân công nhân viên
    @PreAuthorize("hasAnyAuthority('MANAGER','ADMIN')")
    @PostMapping("/assignment")
    public ResponseDTO updateAssignmentBuilding(@RequestBody AssignmentDTO assignmentBuildingDTO) {
        return buildingService.updateAssignmentTable(assignmentBuildingDTO);
    }
}
