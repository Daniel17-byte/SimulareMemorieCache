package com.example.proiectssc.Others;

import com.example.proiectssc.Models.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MemoryRepository extends JpaRepository<Memory, Long> {
    @Query("SELECT m FROM Memory m WHERE m.address = :address")
    Memory getData(@Param("address") Integer address);

    @Transactional
    @Modifying
    @Query("UPDATE Memory m SET m.data = :data WHERE m.address = :address")
    void updateData(@Param("address") Integer address, @Param("data") Integer data);
}
