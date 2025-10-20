package com.example.demo.repository.custom.impl;

import com.example.demo.entity.User;
import com.example.demo.repository.custom.UserRepositoryCustom;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public List<User> findByRole(String roleCode) {
		//JPQL
		String sql = "FROM User";
		Query query = entityManager.createNativeQuery(sql, User.class);
		return query.getResultList();
	}
	@Override
	public List<User> getAllUsers(Pageable pageable) {

		StringBuilder sql = new StringBuilder(buildQueryFilter())
				.append(" LIMIT ").append(pageable.getPageSize()).append("\n")
				.append(" OFFSET ").append(pageable.getOffset());
		System.out.println("Final query: " + sql.toString());

		Query query = entityManager.createNativeQuery(sql.toString(), User.class);
		return query.getResultList();
	}
	@Override
	public int countTotalItem() {
		String sql = buildQueryFilter();
		Query query = entityManager.createNativeQuery(sql.toString());
		return query.getResultList().size();
	}

	private String buildQueryFilter() {
		String sql = "SELECT * FROM user u WHERE u.status = 1";
		return sql;
	}
}
