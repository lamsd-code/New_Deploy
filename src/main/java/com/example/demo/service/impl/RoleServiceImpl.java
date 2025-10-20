package com.example.demo.service.impl;


import com.example.demo.converter.RoleConverter;
import com.example.demo.model.dto.RoleDTO;
import com.example.demo.entity.Role;
import com.example.demo.repository.RoleRepository;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleServiceImpl implements RoleService {
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private RoleConverter roleConverter;

	public List<RoleDTO> findAll() {
		List<Role> roleEntities = roleRepository.findAll();
		List<RoleDTO> list = new ArrayList<>();
		roleEntities.forEach(item -> {
			RoleDTO roleDTO = roleConverter.convertToDto(item);
			list.add(roleDTO);
		});
		return list;
	}

	@Override
	public Map<String, String> getRoles() {
		Map<String,String> roleTerm = new HashMap<>();
		List<Role> roleEntity = roleRepository.findAll();
		for(Role item : roleEntity){
			RoleDTO roleDTO = roleConverter.convertToDto(item);
			roleTerm.put(roleDTO.getCode(), roleDTO.getName());
		}
		return roleTerm;
	}
}
