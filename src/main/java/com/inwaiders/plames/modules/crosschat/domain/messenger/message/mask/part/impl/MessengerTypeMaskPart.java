package com.inwaiders.plames.modules.crosschat.domain.messenger.message.mask.part.impl;

import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.messenger.message.mask.part.impl.MaskPartImpl;
import com.inwaiders.plames.api.messenger.profile.UserProfile;

public class MessengerTypeMaskPart extends MaskPartImpl {
	
	public MessengerTypeMaskPart() {
		
		this.setScopes("[", "]");
	}

	@Override
	public String compile(Message message) {
		
		if(message.getReceiver() instanceof UserProfile && message.getSender() instanceof UserProfile) {
			
			return ((UserProfile) message.getSender()).getMessengerType();
		}
		
		return "unknown messenger";
	}
}
