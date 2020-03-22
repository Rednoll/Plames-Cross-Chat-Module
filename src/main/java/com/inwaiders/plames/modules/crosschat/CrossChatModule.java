package com.inwaiders.plames.modules.crosschat;

import com.inwaiders.plames.api.command.CommandRegistry;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.profile.impl.SystemProfile;
import com.inwaiders.plames.domain.module.impl.ModuleBase;
import com.inwaiders.plames.modules.crosschat.domain.commands.MessengerCommandClose;
import com.inwaiders.plames.modules.crosschat.domain.commands.MessengerCommandRedirect;
import com.inwaiders.plames.modules.crosschat.domain.commands.MessengerCommandSend;
import com.inwaiders.plames.modules.crosschat.domain.commands.MessengerCommandTunnel;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.modules.crosschat.domain.room.commands.RoomCommand;
import com.inwaiders.plames.modules.crosschat.domain.room.commands.RoomCommandAccept;
import com.inwaiders.plames.modules.crosschat.domain.room.commands.RoomCommandCreate;

public class CrossChatModule extends ModuleBase {

	private static final CrossChatModule INSTANCE = new  CrossChatModule();
	
	@Override
	public void preInit() {
		
		CommandRegistry registry = CommandRegistry.getDefaultRegistry();
			registry.registerCommand(new MessengerCommandSend());
			registry.registerCommand(new MessengerCommandTunnel());
			registry.registerCommand(new MessengerCommandClose());
			registry.registerCommand(new MessengerCommandRedirect());
			
			registry.registerCommand(new RoomCommandCreate());
			registry.registerCommand(new RoomCommandAccept());
		
			registry.registerCommand(new RoomCommand((Room room, User target, User invoker) -> room.op(target, invoker), "op"));
			registry.registerCommand(new RoomCommand((Room room, User target, User invoker) -> room.deop(target, invoker), "deop"));
			registry.registerCommand(new RoomCommand((Room room, User target, User invoker) -> room.kick(target, invoker), "kick"));
			registry.registerCommand(new RoomCommand((Room room, User target, User invoker) -> room.invite(target, invoker), "invite"));
	}
	
	@Override
	public void init() {
		
	}
	
	public String getDescription() {
		
		return "module.cross-chat.description";
	}

	@Override
	public String getName() {
		
		return "Cross Chat";
	}

	@Override
	public String getLicenseKey() {
		
		return null;
	}

	@Override
	public long getId() {
		
		return 87475678;
	}

	@Override
	public String getType() {
		
		return "functional";
	}

	@Override
	public String getVersion() {
		
		return "1V";
	}

	@Override
	public long getSystemVersion() {
		
		return 0;
	}
	
	public static SystemProfile getSystemProfile() {
		
		return INSTANCE.getProfile();
	}
	
	public static CrossChatModule getInstance() {
		
		return INSTANCE;
	}
}
