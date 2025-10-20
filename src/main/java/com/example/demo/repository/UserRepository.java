package com.example.demo.repository;


import com.example.demo.entity.Building;
//import com.example.demo.entity.BuildingEntity;
import com.example.demo.entity.User;
import com.example.demo.repository.custom.UserRepositoryCustom;
import org.aspectj.apache.bcel.classfile.Code;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> , UserRepositoryCustom {
    User findOneByUserNameAndStatus(String name, int status);
    Page<User> findByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCaseAndStatusNot(String userName, String fullName, int status,
                                                                                                  Pageable pageable);
    List<User> findByStatusAndRoles_Code(Integer status, String roleCode);
    Page<User> findByStatusNot(int status, Pageable pageable);
    long countByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCaseAndStatusNot(String userName, String fullName, int status);
    long countByStatusNot(int status);
    User findOneByUserName(String userName);
    List<User> findByIdIn(List<Long> id);
    List<User> findByBuildingEntityList(List<Building> buildingEntities);
}
