package com.inwaiders.plames.modules.crosschat.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.crosschat.domain.procedure.TunnelRedirectProcedure;

public class MessengerCommandRedirect extends MessengerCommand{

	public MessengerCommandRedirect() {
	
		this.addAliases("redirect");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		if(args.length == 0) {
			
			if(profile.getCurrentProcedure() != null) throw new CommandException("$procedure.already_runned");
		
			TunnelRedirectProcedure proc = TunnelRedirectProcedure.create(profile);
				proc.begin();
				
			proc.save();
				
			profile.setCurrentProcedure(proc);
			profile.save();
			
			proc.runNextStage();
		}
	}
}
