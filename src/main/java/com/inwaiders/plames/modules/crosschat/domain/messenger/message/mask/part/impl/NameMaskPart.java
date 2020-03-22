package com.inwaiders.plames.modules.crosschat.domain.messenger.message.mask.part.impl;

import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.messenger.message.mask.part.impl.MaskPartImpl;

public class NameMaskPart extends MaskPartImpl {
	
	public NameMaskPart() {
		
		this.setScopes("<", ">");
	}
	
	@Override
	public String compile(Message message) {
		
		return message.getSender().getName();
	}
}
