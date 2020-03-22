package com.inwaiders.plames.modules.crosschat.domain.room.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.crosschat.CrossChatModule;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;
import com.inwaiders.plames.system.utils.MessageUtils;

public class RoomCommandCreate extends MessengerCommand{

	public RoomCommandCreate() {
		
		this.addAliases("create");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		if(args.length != 1) throw new CommandException("$command.create_room.name_nf");
		
		String roomName = args[0];
		User user = profile.getUser();
		
		if(RoomImpl.getByName(roomName) != null) throw new CommandException("$command.create_room.already_exist", roomName);
		
		Room room = RoomImpl.create();
			room.setName(roomName);
			room.setCreator(user);
			
		room.save();
	
		room.joinUser(user);
		user.save();
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), profile, "$command.create_room.success", roomName);
	}
}
