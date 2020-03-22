package com.inwaiders.plames.modules.crosschat.domain.room.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.domain.user.impl.UserImpl;
import com.inwaiders.plames.modules.crosschat.CrossChatModule;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;
import com.inwaiders.plames.system.utils.MessageUtils;

public class RoomCommand extends MessengerCommand{

	private RoomCommandAction action = null;
	
	public RoomCommand(RoomCommandAction action, String... aliases) {
		
		this.addAliases(aliases);
		this.action = action;
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		User invoker = profile.getUser();
		
		String roomName = args[1];
		
		Room room = RoomImpl.getByName(roomName);
		
		if(room == null) {
			
			throw new CommandException("$room.not_found", roomName);
		}
		
		String targetName = args[0];
	
		User target = UserImpl.getByNickname(targetName);
	
		if(target == null) {
			
			throw new CommandException("$user.not_found", targetName);
		}
	
		DescribedFunctionResult result = action.run(room, target, invoker);
		String description = result.getDescription();
		
		if(description != null && !description.isEmpty()) {
			
			MessageUtils.send(CrossChatModule.getSystemProfile(), profile, description);
		}
	}
	
	public static interface RoomCommandAction {
		
		public DescribedFunctionResult run(Room room, User target, User invoker);
	}
}
