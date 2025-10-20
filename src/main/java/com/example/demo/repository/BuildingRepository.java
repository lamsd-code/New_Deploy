package com.example.demo.repository;


import com.example.demo.entity.Building;
import com.example.demo.repository.custom.BuildingRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long>, BuildingRepositoryCustom {
    void deleteAllByIdIn(List<Long> buildingIds);
    @Query("SELECT b FROM Building b JOIN b.userEntities u WHERE u.id = :staffId")
    List<Building> findByStaffId(@Param("staffId") Long staffId);
    
}
