package com.inwaiders.plames.modules.crosschat.domain.messenger.message.mask.impl;

import com.inwaiders.plames.api.messenger.message.mask.impl.MessageMaskImpl;
import com.inwaiders.plames.modules.crosschat.domain.messenger.message.mask.part.impl.MessengerTypeMaskPart;
import com.inwaiders.plames.modules.crosschat.domain.messenger.message.mask.part.impl.NameMaskPart;

public class DefaultMessageMask extends MessageMaskImpl{

	private static DefaultMessageMask instance = new DefaultMessageMask();
	
	private DefaultMessageMask() {
		
		this.prefixes.add(new MessengerTypeMaskPart());
		this.prefixes.add(new NameMaskPart());
	}
	
	public static DefaultMessageMask getInstance() {
		
		return instance;
	}
}
