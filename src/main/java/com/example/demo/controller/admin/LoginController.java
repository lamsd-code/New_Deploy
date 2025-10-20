package com.example.demo.controller.admin;

import com.example.demo.constant.SystemConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "loginControllerOfAdmin")
public class LoginController {

    @GetMapping("/admin/login")
    public ModelAndView loginPage(HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("admin/login");
        String error = request.getParameter("error");
        String logout = request.getParameter("logout");

        if (error != null) {
            mav.addObject(SystemConstant.ALERT, "danger");
            mav.addObject(SystemConstant.MESSAGE_RESPONSE, "Tên đăng nhập hoặc mật khẩu không chính xác!");
        }

        if (logout != null) {
            mav.addObject(SystemConstant.ALERT, "success");
            mav.addObject(SystemConstant.MESSAGE_RESPONSE, "Đăng xuất thành công!");
        }

        return mav;
    }
}
