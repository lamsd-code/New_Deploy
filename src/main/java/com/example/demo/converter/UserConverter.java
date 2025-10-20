package com.example.demo.converter;

import com.example.demo.model.dto.UserDTO;
import com.example.demo.entity.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {

    @Autowired
    private ModelMapper modelMapper;

    public UserDTO convertToDto (User entity){
        UserDTO result = modelMapper.map(entity, UserDTO.class);
        return result;
    }

    public User convertToEntity (UserDTO dto){
        User result = modelMapper.map(dto, User.class);
        return result;
    }
    
}
