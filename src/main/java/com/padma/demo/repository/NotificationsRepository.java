package com.padma.demo.repository;

import java.util.List;

import com.padma.demo.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationsRepository extends JpaRepository<Notification, String> {

    List<Notification> findByUser_Id(String user_id);
    List<Notification> findByUser_IdAndReadFalse(String user_id);
}