package com.inwaiders.plames.modules.crosschat.domain.room.message;

import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.domain.messenger.message.impl.MessageImpl;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;

public class RoomMessageStub extends MessageImpl {

	private Room room = null;
	
	public RoomMessageStub(Room room, UserProfile target, Message message) {
		super();
	
		this.setCreationDate(message.getCreationDate());
		this.setText(message.getText());
		
		this.setSender(message.getSender());
		this.setReceiver(target);
		
		this.setRoom(room);
	}
	
	public void setRoom(Room room) {
		
		this.room = room;
	}
	
	public Room getRoom() {
		
		return this.room;
	}
	
	@Override
	public void save() {
		
	}
	
	@Override
	public void delete() {
		
	}
}
