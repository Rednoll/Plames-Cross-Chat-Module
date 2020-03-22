package com.inwaiders.plames.modules.crosschat.domain.procedure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.messenger.message.MessageReceiver;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.messenger.profile.additions.ProfilesContainer;
import com.inwaiders.plames.api.procedure.Procedure;
import com.inwaiders.plames.api.procedure.ProcedureStage;
import com.inwaiders.plames.api.procedure.ProcedureStageRunResult;
import com.inwaiders.plames.api.procedure.ProcedureStageStatus;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.message.impl.MessageImpl;
import com.inwaiders.plames.domain.messenger.profile.impl.UserProfileBase;
import com.inwaiders.plames.domain.messenger.profile.procedures.ProfileProcedure;
import com.inwaiders.plames.modules.crosschat.CrossChatModule;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.system.utils.MessageUtils;

@Entity(name = "TunnelProcedure")
@Table(name = "tunnel_procedures")
public class TunnelProcedure extends ProfileProcedure<ProcedureStage<TunnelProcedure>>{

	@JoinColumn(name = "receiver_id")
	@ManyToOne(targetEntity = UserProfileBase.class)
	private MessageReceiver receiver = null;

	private TunnelProcedure(UserProfile profile, MessageReceiver targetDeu) {
		super();
		
		this.profile = profile;
		this.receiver = targetDeu;
	}
	
	public TunnelProcedure() {
		
		this.getStages().add(new ProcedureStage<TunnelProcedure>() {
			
			@Override
			public ProcedureStageRunResult run(TunnelProcedure procedure, String... args) {

				String text = String.join(" ", args);
				
				Message message = MessageImpl.create();
					message.setCreationDate(System.currentTimeMillis());
					message.setSender(profile);
					message.setReceiver(receiver);
					message.setText(text);
				
				message.save();
				message.send();
				
				return new ProcedureStageRunResult(ProcedureStageStatus.REPEAT);
			}
		});
	}
	
	public void redirect(MessageReceiver newTarget) {
		
		this.setTargetDeu(newTarget);
		this.save();
	}
	
	public void changeSource(UserProfile newSource) {
		
		this.profile.setCurrentProcedure(null);
		this.save();
		
		newSource.setCurrentProcedure(this);
		newSource.save();
		
		this.profile = newSource;
		this.save();
	}
	
	@Override
	public String toString() {
		
		if(receiver instanceof UserProfile) {
			
			return "["+profile.getMessengerType()+"] -> ["+((UserProfile) receiver).getMessengerType()+"] <"+receiver.getName()+">";
		}
		else if(receiver instanceof Room){
			
			return "["+profile.getMessengerType()+"] -> "+receiver.getName()+"";
		}
		
		return null;
	}
	
	public void onBegin() {
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), profile, "$tunnel.opened");
	}
	
	public void onFail() {
	
		MessageUtils.send(CrossChatModule.getSystemProfile(), profile, "$tunnel.closed");
	}
	
	public void onEnd() {
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), profile, "$tunnel.closed");
	}

	public void setTargetDeu(MessageReceiver deu) {
		
		this.receiver = deu;
	}
	
	public MessageReceiver getTargetDeu() {
		
		return this.receiver;
	}
	
	public static TunnelProcedure getTunnelByDeu(Collection<TunnelProcedure> tunnels, MessageReceiver deu) {
		
		for(TunnelProcedure tunnel : tunnels) {
			
			if(tunnel.getTargetDeu().equals(deu)) {
				
				return tunnel;
			}
		}
		
		return null;
	}
	
	public static List<TunnelProcedure> getTunnels(User user) {
	
		List<TunnelProcedure> tunnels = new ArrayList<>();
		
		ProfilesContainer profilesContainer = user.getProfilesContainer();
		
		for(UserProfile profile : profilesContainer.getProfiles()) {
			
			Procedure proc = profile.getCurrentProcedure();
			
			if(proc instanceof TunnelProcedure) {
		
				tunnels.add((TunnelProcedure) proc);
			}
		}
		
		return tunnels;
	}
	
	public static TunnelProcedure create(UserProfile profile, MessageReceiver deu) {
		
		TunnelProcedure procedure = new TunnelProcedure(profile, deu);

		procedure = repository.saveAndFlush(procedure);
		
		return procedure;
	}
}
