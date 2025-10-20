package com.example.demo.controller.admin;

import com.example.demo.constant.SystemConstant;
import com.example.demo.converter.BuildingDTOConverter;
import com.example.demo.enums.BuildingType;
import com.example.demo.enums.DistrictCode;
import com.example.demo.model.dto.BuildingDTO;
import com.example.demo.model.request.BuildingSearchRequest;
import com.example.demo.model.response.BuildingSearchResponse;
import com.example.demo.security.utils.SecurityUtils;
import com.example.demo.service.BuildingService;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Transactional
@RestController(value="buildingControllerOfAdmin")
public class  BuildingController {
    @Autowired
    private UserService userService;
    @Autowired
    private BuildingService buildingService;
    @Autowired
    private BuildingDTOConverter buildingDTOConverter;

    @GetMapping(value = "/admin/building-list")
    public ModelAndView buildingList(@ModelAttribute BuildingSearchRequest buildingSearchRequest,
                                     @RequestParam Map<String, Object> conditions,
                                     @RequestParam(name = "typeCode", required = false) List<String> typeCode,
                                     @ModelAttribute(SystemConstant.MODEL) BuildingDTO model,
                                     HttpServletRequest request) {

        // ✅ Lấy thông tin người dùng hiện tại
        Long currentUserId = SecurityUtils.getPrincipal().getId();
        List<String> roles = SecurityUtils.getAuthorities();

        // ✅ Nếu là STAFF → chỉ xem tòa nhà được giao
        if (roles.contains(SystemConstant.STAFF_ROLE)) {
            conditions.put("staffId", currentUserId);
        }

        // ✅ Gọi service để lấy danh sách tòa nhà
        List<BuildingSearchResponse> responseList = buildingService.findAll(conditions, typeCode);

        // ✅ Phân trang
        model.setMaxPageItems(5);
        model.setTotalItem(responseList.size());

        // ✅ Gửi dữ liệu sang View
        ModelAndView mav = new ModelAndView("admin/building/list");
        mav.addObject("modelSearch", buildingSearchRequest);
        mav.addObject("buildingList", responseList);

        // ✅ Chỉ MANAGER và ADMIN mới thấy danh sách nhân viên
        if (roles.contains(SystemConstant.MANAGER_ROLE) || roles.contains(SystemConstant.ADMIN_ROLE)) {
            mav.addObject("staffs", userService.getStaffs());
        }

        mav.addObject("districts", DistrictCode.type());
        mav.addObject("typeCodes", BuildingType.type());
        return mav;
    }

    @GetMapping(value = "/admin/building-edit")
    public ModelAndView buildingEdit(@ModelAttribute BuildingDTO buildingDTO, HttpServletRequest request){
        ModelAndView mav = new ModelAndView("admin/building/edit");

        mav.addObject("buildingEdit", buildingDTO);
        mav.addObject("districts", DistrictCode.type());
        mav.addObject("typeCodes", BuildingType.type());
        return mav;
    }
    @GetMapping(value = "/admin/building-edit-{id}")
    public ModelAndView buildingEdit(@PathVariable("id") Long id, HttpServletRequest request){
        ModelAndView mav = new ModelAndView("admin/building/edit");
        BuildingDTO buildingDTO = buildingService.findBuildingById(id);
        mav.addObject("buildingEdit", buildingDTO);
        mav.addObject("districts", DistrictCode.type());
        mav.addObject("typeCodes", BuildingType.type());
        return mav;
    }

}
