package com.inwaiders.plames.modules.crosschat.dao.room;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;

@Service
public class RoomRepositoryInjector {

	@Autowired
	private RoomRepository repository;
	
	@PostConstruct
	private void inject() {
		
		RoomImpl.setRepository(repository);
	}
}
