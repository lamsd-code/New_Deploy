package com.example.demo.service.impl;

import com.example.demo.constant.SystemConstant;
import com.example.demo.converter.UserConverter;
import com.example.demo.model.dto.PasswordDTO;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.MyException;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserConverter userConverter;

    @Override
    public UserDTO findOneByUserNameAndStatus(String name, int status) {
        return userConverter.convertToDto(
                userRepository.findOneByUserNameAndStatus(name, status)
        );
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User entity = userConverter.convertToEntity(userDTO);
        entity.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        entity.setStatus(1);
        userRepository.save(entity);
        return userConverter.convertToDto(entity);
    }

    @Override
    public List<UserDTO> getUsers(String searchValue, Pageable pageable) {
        Page<User> users;
        if (StringUtils.isNotBlank(searchValue)) {
            users = userRepository.findByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCaseAndStatusNot(
                    searchValue, searchValue, 0, pageable);
        } else {
            users = userRepository.findByStatusNot(0, pageable);
        }

        List<User> entities = users.getContent();
        List<UserDTO> result = new ArrayList<>();
        for (User userEntity : entities) {
            UserDTO dto = userConverter.convertToDto(userEntity);
            if (!userEntity.getRoles().isEmpty()) {
                dto.setRoleCode(userEntity.getRoles().get(0).getCode());
            }
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<UserDTO> getAllUsers(Pageable pageable) {
        List<User> userEntities = userRepository.getAllUsers(pageable);
        List<UserDTO> results = new ArrayList<>();
        for (User userEntity : userEntities) {
            UserDTO dto = userConverter.convertToDto(userEntity);
            if (!userEntity.getRoles().isEmpty()) {
                dto.setRoleCode(userEntity.getRoles().get(0).getCode());
            }
            results.add(dto);
        }
        return results;
    }

    @Override
    public Map<Long, String> getStaffs() {
        Map<Long, String> listStaffs = new HashMap<>();
        List<User> staffs = userRepository.findByStatusAndRoles_Code(1, "STAFF");
        for (User it : staffs) {
            listStaffs.put(it.getId(), it.getFullName());
        }
        return listStaffs;
    }

    @Override
    public int countTotalItems() {
        return userRepository.countTotalItem();
    }

    @Override
    public int getTotalItems(String searchValue) {
        if (StringUtils.isNotBlank(searchValue)) {
            return (int) userRepository.countByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCaseAndStatusNot(
                    searchValue, searchValue, 0);
        } else {
            return (int) userRepository.countByStatusNot(0);
        }
    }

    @Override
    public UserDTO findOneByUserName(String userName) {
        User entity = userRepository.findOneByUserName(userName);
        return userConverter.convertToDto(entity);
    }

    @Override
    public UserDTO findUserById(long id) {
        User entity = userRepository.findById(id).get();
        UserDTO dto = userConverter.convertToDto(entity);
        entity.getRoles().forEach(role -> dto.setRoleCode(role.getCode()));
        return dto;
    }

    @Override
    @Transactional
    public UserDTO insert(UserDTO newUser) {
        Role role = roleRepository.findOneByCode(newUser.getRoleCode());
        User userEntity = userConverter.convertToEntity(newUser);
        userEntity.setRoles(Stream.of(role).collect(Collectors.toList()));
        userEntity.setStatus(1);
        userEntity.setPassword(passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT));
        return userConverter.convertToDto(userRepository.save(userEntity));
    }

    @Override
    @Transactional
    public UserDTO update(Long id, UserDTO updateUser) {
        Role role = roleRepository.findOneByCode(updateUser.getRoleCode());
        User oldUser = userRepository.findById(id).get();
        User userEntity = userConverter.convertToEntity(updateUser);

        userEntity.setUserName(oldUser.getUserName());
        userEntity.setStatus(oldUser.getStatus());
        userEntity.setRoles(Stream.of(role).collect(Collectors.toList()));
        userEntity.setPassword(oldUser.getPassword());

        return userConverter.convertToDto(userRepository.save(userEntity));
    }

    @Override
    @Transactional
    public void updatePassword(long id, PasswordDTO passwordDTO) throws MyException {
        User user = userRepository.findById(id).get();
        if (passwordEncoder.matches(passwordDTO.getOldPassword(), user.getPassword())
                && passwordDTO.getNewPassword().equals(passwordDTO.getConfirmPassword())) {
            user.setPassword(passwordEncoder.encode(passwordDTO.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new MyException(SystemConstant.CHANGE_PASSWORD_FAIL);
        }
    }

    @Override
    @Transactional
    public UserDTO resetPassword(long id) {
        User entity = userRepository.findById(id).get();
        entity.setPassword(passwordEncoder.encode(SystemConstant.PASSWORD_DEFAULT));
        return userConverter.convertToDto(userRepository.save(entity));
    }

    @Override
    @Transactional
    public UserDTO updateProfileOfUser(String username, UserDTO updateUser) {
        User oldUser = userRepository.findOneByUserName(username);
        oldUser.setFullName(updateUser.getFullName());
        return userConverter.convertToDto(userRepository.save(oldUser));
    }

    @Override
    @Transactional
    public void delete(long[] ids) {
        for (Long id : ids) {
            User userEntity = userRepository.findById(id).get();
            userEntity.setStatus(0);
            userRepository.save(userEntity);
        }
    }
}
