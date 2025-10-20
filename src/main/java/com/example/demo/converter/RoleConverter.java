package com.example.demo.converter;


import com.example.demo.model.dto.RoleDTO;
import com.example.demo.entity.Role;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RoleConverter {
	
	@Autowired
	private ModelMapper modelMapper;
	
	public RoleDTO convertToDto(Role entity) {
		RoleDTO result = modelMapper.map(entity, RoleDTO.class);
        return result;
    }

    public Role convertToEntity(RoleDTO dto) {
    	Role result = modelMapper.map(dto, Role.class);
        return result;
    }
}

