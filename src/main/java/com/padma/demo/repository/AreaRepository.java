package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Area;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByNameIgnoreCase(String name);

    List<Area> findAllByUsers_UserId(Long userId);

}
