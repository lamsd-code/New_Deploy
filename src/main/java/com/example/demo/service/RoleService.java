package com.example.demo.service;


import com.example.demo.model.dto.RoleDTO;

import java.util.List;
import java.util.Map;

public interface RoleService {
	List<RoleDTO> findAll();
	Map<String,String> getRoles();
}
