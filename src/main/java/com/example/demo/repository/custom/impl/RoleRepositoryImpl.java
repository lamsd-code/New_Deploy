package com.example.demo.repository.custom.impl;

import com.example.demo.entity.Role;
import com.example.demo.repository.custom.RoleRepositoryCustom;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleRepositoryImpl implements RoleRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role findOneByCode(String code) {
        String sql = "SELECT * FROM role r WHERE r.code = :code";
        Query query = entityManager.createNativeQuery(sql, Role.class);
        query.setParameter("code", code);
        List<Role> result = query.getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<Role> findAllRoles() {
        String sql = "SELECT * FROM role";
        Query query = entityManager.createNativeQuery(sql, Role.class);
        return query.getResultList();
    }
}
