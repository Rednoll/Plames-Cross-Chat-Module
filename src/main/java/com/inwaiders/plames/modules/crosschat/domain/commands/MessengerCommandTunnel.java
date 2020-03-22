package com.inwaiders.plames.modules.crosschat.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.message.MessageReceiver;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.domain.user.impl.UserImpl;
import com.inwaiders.plames.modules.crosschat.domain.procedure.TunnelProcedure;
import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;

public class MessengerCommandTunnel extends MessengerCommand{

	public MessengerCommandTunnel() {

		this.addAliases("tunnel");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		if(profile.getCurrentProcedure() != null) throw new CommandException("$procedure.already_runned");
		
		String targetName = args[0];
		
		if(args.length == 2) {
			
			String targetMessengerType = args[1];
			
			run(profile, targetName, targetMessengerType);	
		}
		else {
			
			run(profile, targetName);
		}
	}
	
	public void run(UserProfile sourceProfile, String targetRoomName) throws CommandException {
		
		MessageReceiver targetDeu = null;
		
		targetDeu = RoomImpl.getByName(targetRoomName);
		
		if(targetDeu == null) {
			
			throw new CommandException("$room.not_found", targetRoomName);
		}
		
		run(sourceProfile, targetDeu);
	}
	
	public void run(UserProfile sourceProfile, String targetUserName, String targetMessengerType) throws CommandException {
		
		User user = UserImpl.getByNickname(targetUserName);
		
		if(user == null) {
			
			throw new CommandException("$user.not_found", targetUserName);
		}
		
		UserProfile profile = user.getProfilesContainer().getOneProfile(targetMessengerType);
		
		if(profile == null) {
			
			throw new CommandException("$command.send.target_profile_nf", targetUserName, targetMessengerType);
		}
		
		run(sourceProfile, profile);
	}
	
	public void run(UserProfile profile, MessageReceiver deu) throws CommandException {
		
		if(deu == null) {
			
			throw new CommandException("$system.runtime_exception");
		}
		
		TunnelProcedure procedure = TunnelProcedure.create(profile, deu);
			procedure.begin();
		
		profile.setCurrentProcedure(procedure);
		profile.save();
	}
}
