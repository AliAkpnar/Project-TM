package com.aliakpinar.project.service;

import java.util.List;

import com.aliakpinar.project.model.Role;

public interface RoleService {
    Role createRole(Role role);

    List<Role> findAll();
}
