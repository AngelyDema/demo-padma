package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Area;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, String> {

    List<Area> findByNameIgnoreCase(String name);
    List<Area> findByUsers(Long users);

}
