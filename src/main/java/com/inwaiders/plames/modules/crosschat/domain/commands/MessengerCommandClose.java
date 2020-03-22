package com.inwaiders.plames.modules.crosschat.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.procedure.Procedure;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.crosschat.domain.procedure.TunnelProcedure;

public class MessengerCommandClose extends MessengerCommand{

	public MessengerCommandClose() {
	
		this.addAliases("close");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		if(args.length == 0) {
			
			closeTunnel(profile);
		}
	}
	
	public void closeTunnel(UserProfile profile) throws CommandException {
		
		Procedure procedure = profile.getCurrentProcedure();
	
		if(procedure instanceof TunnelProcedure) {
		
			profile.setCurrentProcedure(null);
			profile.save();
			
			procedure.abort();
			procedure.delete();
		}
		else {
			
			throw new CommandException("$command.close_tunnel.procedure_not_tunnel");
		}
	}
}
