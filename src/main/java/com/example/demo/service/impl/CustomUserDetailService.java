package com.example.demo.service.impl;

import com.example.demo.model.dto.MyUserDetail;
import com.example.demo.model.dto.UserDTO;
import com.example.demo.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.entity.Role;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        System.out.println("🔍 [DEBUG] Đang gọi loadUserByUsername với: " + name);
        UserDTO userDTO = userService.findOneByUserNameAndStatus(name, 1);
        if (userDTO == null) {
            System.out.println("❌ [DEBUG] Không tìm thấy user hoặc status != 1");
            throw new UsernameNotFoundException("Username not found");
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : userDTO.getRoles()) {
            // ✅ KHÔNG thêm tiền tố "ROLE_" vì DB đã lưu đúng dạng "MANAGER", "STAFF"
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
        }

        // 🔍 Debug role thực tế
        System.out.println("🧩 [DEBUG] User đăng nhập: " + name + " | Roles: " + authorities);

        MyUserDetail myUserDetail =
                new MyUserDetail(name, userDTO.getPassword(), true, true, true, true, authorities);
        BeanUtils.copyProperties(userDTO, myUserDetail);
        return myUserDetail;
    }
}
