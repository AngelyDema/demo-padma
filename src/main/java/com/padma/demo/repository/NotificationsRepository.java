package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationsRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUsers(Long users);
    List<Notification> findByUsersAndReadFalse(Long users);
}