package com.example.demo.repository.custom;

import com.example.demo.entity.Role;
import java.util.List;

public interface RoleRepositoryCustom {
    Role findOneByCode(String code);
    List<Role> findAllRoles();
}
