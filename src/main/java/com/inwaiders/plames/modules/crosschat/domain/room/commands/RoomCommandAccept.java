package com.inwaiders.plames.modules.crosschat.domain.room.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.crosschat.CrossChatModule;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;
import com.inwaiders.plames.system.utils.MessageUtils;

public class RoomCommandAccept extends MessengerCommand{

	public RoomCommandAccept() {
		
		this.addAliases("accept");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		User user = profile.getUser();
		
		String roomName = args[0];
	
		Room room = RoomImpl.getByName(roomName);
	
		if(room == null) {
			
			throw new CommandException("$room.not_found", roomName);
		}
			
		DescribedFunctionResult result = room.inviteAccepted(user);
		String description = result.getDescription();
		
		if(result.getStatus() == Status.OK) {
			
			room.introduceUser(user);
		}
		
		if(description != null && description.isEmpty()) {
			
			MessageUtils.send(CrossChatModule.getSystemProfile(), profile, description);
		}
	}
}
