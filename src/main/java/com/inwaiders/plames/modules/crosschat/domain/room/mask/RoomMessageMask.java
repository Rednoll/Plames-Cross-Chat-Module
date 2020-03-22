package com.inwaiders.plames.modules.crosschat.domain.room.mask;

import com.inwaiders.plames.api.messenger.message.mask.impl.MessageMaskImpl;
import com.inwaiders.plames.modules.crosschat.domain.messenger.message.mask.part.impl.NameMaskPart;

public class RoomMessageMask extends MessageMaskImpl{

	private static RoomMessageMask instance = new RoomMessageMask();
	
	public RoomMessageMask() {
		
		this.prefixes.add(new NameMaskPart());
	}
	
	public static RoomMessageMask getInstance() {
		
		return instance;
	}
}
