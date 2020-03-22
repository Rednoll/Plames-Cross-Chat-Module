package com.inwaiders.plames.modules.crosschat.domain.room.mask.part.impl;

import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.messenger.message.mask.part.impl.MaskPartImpl;
import com.inwaiders.plames.modules.crosschat.domain.room.message.RoomMessageStub;

public class RoomMessageMaskPart extends MaskPartImpl {
	
	public RoomMessageMaskPart() {
		
		this.setScopes("[", "]");
	}
	
	@Override
	public String compile(Message message) {
		
		if(message instanceof RoomMessageStub) {
			
			return ((RoomMessageStub) message).getRoom().getName();
		}
	
		return "unknown room";
	}
}