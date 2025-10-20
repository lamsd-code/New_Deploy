package com.example.demo.security;

import com.example.demo.entity.User;
import com.example.demo.model.dto.MyUserDetail;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * ✅ Hàm loadUserByUsername — được Spring Security gọi khi user đăng nhập.
     * Dùng @Transactional để tránh lỗi LazyInitialization khi load roles.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userEntity = userRepository.findOneByUserName(username);

        if (userEntity == null) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản: " + username);
        }

        // ⚙️ Ép Hibernate load roles nếu là Lazy
        userEntity.getRoles().size(); // Bắt buộc Hibernate load roles trong cùng transaction

        // ⚠️ Nếu tài khoản không có role, báo lỗi
        if (userEntity.getRoles() == null || userEntity.getRoles().isEmpty()) {
            throw new UsernameNotFoundException("Tài khoản chưa được gán vai trò nào!");
        }

        // 🧩 Chuyển roles → GrantedAuthority
        List<GrantedAuthority> authorities = new ArrayList<>();
        userEntity.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getCode()));
        });

        // 🔑 Tạo MyUserDetail để Spring Security sử dụng
        MyUserDetail myUserDetail = new MyUserDetail(
                userEntity.getUserName(),
                userEntity.getPassword(),
                true, true, true, true,
                authorities
        );
        myUserDetail.setId(userEntity.getId());
        myUserDetail.setFullName(userEntity.getFullName());

        return myUserDetail;
    }

}
