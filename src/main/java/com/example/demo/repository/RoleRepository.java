package com.example.demo.repository;

import com.example.demo.entity.Role;
import com.example.demo.repository.custom.RoleRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, RoleRepositoryCustom {
    // JpaRepository tự sinh CRUD: save, findById, findAll, deleteById, ...
    // RoleRepositoryCustom cho phép viết thêm query tùy chỉnh
}
