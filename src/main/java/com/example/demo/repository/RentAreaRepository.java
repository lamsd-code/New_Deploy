package com.example.demo.repository;


import com.example.demo.entity.RentArea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RentAreaRepository extends JpaRepository<RentArea, Long> {
    void deleteBybuilding_IdIn(List<Long> buildingIds);
    void deleteByBuildingId(Long buildingId);
}
