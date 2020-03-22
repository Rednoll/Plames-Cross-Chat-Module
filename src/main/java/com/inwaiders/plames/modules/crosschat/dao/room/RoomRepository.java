package com.inwaiders.plames.modules.crosschat.dao.room;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;

@Repository
public interface RoomRepository extends JpaRepository<RoomImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT r FROM Room r WHERE r.name = :name AND r.deleted != true")
	public RoomImpl getByName(@Param("name") String name);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT r FROM Room r WHERE r.id = :id AND r.deleted != true")
	public RoomImpl getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT r FROM Room r WHERE r.deleted != true")
	public List<RoomImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Room r WHERE r.deleted != true")
	public long count();
}
