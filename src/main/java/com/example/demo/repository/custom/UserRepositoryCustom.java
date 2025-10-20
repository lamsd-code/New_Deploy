package com.example.demo.repository.custom;


import com.example.demo.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
	List<User> findByRole(String roleCode);
	List<User> getAllUsers(Pageable pageable);
	int countTotalItem();
}
