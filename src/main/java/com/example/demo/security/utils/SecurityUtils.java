package com.example.demo.security.utils;

import com.example.demo.model.dto.MyUserDetail;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * ✅ SecurityUtils — tiện ích truy xuất người dùng đăng nhập hiện tại.
 * An toàn với mọi trạng thái (đã đăng nhập, anonymous, null context)
 */
public class SecurityUtils {

    /**
     * 🧩 Lấy thông tin người dùng hiện tại (MyUserDetail).
     * @return MyUserDetail nếu user đã đăng nhập, null nếu chưa.
     */
    public static MyUserDetail getPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // ❌ Không có context hoặc chưa đăng nhập
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        // 🚫 Anonymous user (Spring tạo khi chưa login)
        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        // ✅ Trường hợp hợp lệ: đã đăng nhập
        Object principal = auth.getPrincipal();
        if (principal instanceof MyUserDetail) {
            return (MyUserDetail) principal;
        }

        return null;
    }

    /**
     * 🔐 Lấy danh sách quyền (ROLE_xxx) của người dùng hiện tại.
     * @return Danh sách chuỗi quyền, rỗng nếu chưa login.
     */
    public static List<String> getAuthorities() {
        List<String> results = new ArrayList<>();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return results;
        }

        for (GrantedAuthority authority : auth.getAuthorities()) {
            results.add(authority.getAuthority());
        }
        return results;
    }

    /**
     * ⚙️ Kiểm tra nhanh xem người dùng có vai trò cụ thể không.
     * @param role mã quyền (VD: "ROLE_ADMIN")
     * @return true nếu có, false nếu không hoặc chưa login.
     */
    public static boolean hasRole(String role) {
        return getAuthorities().contains(role);
    }

    /**
     * 🚪 Kiểm tra user hiện tại đã đăng nhập chưa.
     * @return true nếu đã login, false nếu anonymous.
     */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }
}
