package com.inwaiders.plames.modules.crosschat.domain.commands;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.messenger.message.MessageReceiver;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.domain.messenger.message.impl.MessageImpl;
import com.inwaiders.plames.domain.user.impl.UserImpl;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;

public class MessengerCommandSend extends MessengerCommand{
	
	public MessengerCommandSend() {

		this.addAliases("send");
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void run(UserProfile sourceProfile, String... args) throws CommandException{
		
		String receiverNickname = args[0];
		String targetMessengerType = args.length >= 2 ? args[1] : null;
		
		StringBuilder textBuilder = new StringBuilder();
		
		for(int i = 2;i<args.length;i++) {
			
			textBuilder.append(" "+args[i]);
		}
		
		String text = textBuilder.toString().trim();
		
		run(sourceProfile, receiverNickname, targetMessengerType, text);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void run(UserProfile sourceProfile, String receiverName, String targetMessengerType, String text) throws CommandException{

		Room room = RoomImpl.getByName(receiverName);
		
		if(room != null) {
			
			run(sourceProfile, room, text);
		}
		else {
		
			User user = UserImpl.getByNickname(receiverName);
			
			if(user == null) {
				
				throw new CommandException("$command.send.deu_not_found", receiverName);
			}
			
			UserProfile profile = user.getProfilesContainer().getOneProfile(targetMessengerType);
		
			if(profile == null) {
				
				throw new CommandException("$command.send.target_profile_nf", receiverName, targetMessengerType);
			}
			
			run(sourceProfile, profile, text);
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void run(UserProfile sourceProfile, MessageReceiver target, String text) throws CommandException{
	
		Message message = MessageImpl.create();
			message.setSender(sourceProfile);
			message.setReceiver(target);
			message.setCreationDate(System.currentTimeMillis());
			message.setText(text);
			
		message.save();
		message.send();
	}
}
