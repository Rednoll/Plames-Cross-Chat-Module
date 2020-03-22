package com.inwaiders.plames.modules.crosschat.domain.room.additions;

import java.util.Set;

import com.inwaiders.plames.modules.crosschat.domain.room.Room;

public interface RoomsContainer extends Set<Room>{

	public Room getByName(String name);
	public void setRooms(Set<Room> rooms);
	public Set<Room> getRooms();
}
