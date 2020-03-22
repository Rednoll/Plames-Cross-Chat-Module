package com.inwaiders.plames.modules.crosschat.domain.procedure;

import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.inwaiders.plames.api.messenger.message.MessageReceiver;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.procedure.ProcedureStage;
import com.inwaiders.plames.api.procedure.ProcedureStageRunResult;
import com.inwaiders.plames.api.procedure.ProcedureStageStatus;
import com.inwaiders.plames.domain.messenger.profile.impl.UserProfileBase;
import com.inwaiders.plames.domain.procedure.impl.ProcedureImpl;
import com.inwaiders.plames.modules.crosschat.CrossChatModule;
import com.inwaiders.plames.system.utils.MessageUtils;

@Entity
@Table(name = "tunnel_redirect_procedures")
public class TunnelRedirectProcedure extends ProcedureImpl<ProcedureStage<TunnelRedirectProcedure>>{	
	
	@JoinColumn(name = "new_source_id")
	@ManyToOne(targetEntity = UserProfileBase.class)
	private UserProfile newSource = null;

	@Transient
	private List<TunnelProcedure> tunnels = null;
	
	private TunnelRedirectProcedure(UserProfile newSource) {
		this();
		
		this.newSource = newSource;
	}
	
	public TunnelRedirectProcedure() {
		
		this.getStages().add(new ProcedureStage<TunnelRedirectProcedure>() {
			
			@Override
			public ProcedureStageRunResult run(TunnelRedirectProcedure procedure, String... args) {
				
				List<TunnelProcedure> tunnels = procedure.getTunnels();

				if(procedure.tunnels.size() == 0) {
					
					MessageUtils.send(CrossChatModule.getSystemProfile(), procedure.newSource, "$procedure.redirect.tunnel_nf");
			
					return new ProcedureStageRunResult(ProcedureStageStatus.ABORT);
				}
				else if(tunnels.size() == 1) {
					
					TunnelProcedure tunnel = procedure.tunnels.iterator().next();

					redirect(tunnel);

					return new ProcedureStageRunResult(ProcedureStageStatus.COMPLETE);
				}
				else if(procedure.tunnels.size() > 1) {
					
					StringBuilder builder = new StringBuilder(procedure.newSource.getUser().getLocale().getMessage("procedure.redirect.tunnel_index_req")+"\n");
					
						int counter = 0;
						
						for(TunnelProcedure tunnel : procedure.tunnels) {
						
							counter++;
							builder.append("	"+counter+". "+tunnel.toString()+"\n");
						}
					
					String text = builder.toString();
				
					MessageUtils.send(CrossChatModule.getSystemProfile(), procedure.newSource, text);
					
					return new ProcedureStageRunResult(ProcedureStageStatus.OK);
				}
				
				return new ProcedureStageRunResult(ProcedureStageStatus.OK);
			}
		});
		
		this.getStages().add(new ProcedureStage<TunnelRedirectProcedure>() {
			
			@Override
			public ProcedureStageRunResult run(TunnelRedirectProcedure procedure, String... args) {
				
				List<TunnelProcedure> tunnels = procedure.getTunnels();
				
				String rawNumber = args[0];
				
				try {
					
					int number = Integer.valueOf(rawNumber);
					
					if(number > tunnels.size()) {
						
						MessageUtils.send(CrossChatModule.getSystemProfile(), procedure.newSource, "$procedure.redirect.numbered_tunnel_nf");
						
						return new ProcedureStageRunResult(ProcedureStageStatus.REPEAT);
					}
					else {
						
						number -= 1; // cause for user list start from 1
						
						TunnelProcedure tunnel = tunnels.get(number);

						redirect(tunnel);
						
						return new ProcedureStageRunResult(ProcedureStageStatus.COMPLETE);
					}
				}
				catch(NumberFormatException e) {
					
					MessageUtils.send(CrossChatModule.getSystemProfile(), procedure.newSource, "$procedure.redirect.tunnel_index_info");
					
					return new ProcedureStageRunResult(ProcedureStageStatus.REPEAT);
				}
			}
		});
	}
	
	@Override
	public void onBegin() {
		
		this.tunnels = TunnelProcedure.getTunnels(this.newSource.getUser());
		Collections.sort(this.tunnels, (TunnelProcedure o1, TunnelProcedure o2) -> o1.getId().compareTo(o2.getId()));
	}
	
	private void redirect(TunnelProcedure tunnel) {
		
		UserProfile oldSource = tunnel.getProfile();
		
		tunnel.changeSource(this.newSource);
		tunnel.save();
		
		MessageReceiver deu = tunnel.getTargetDeu();
	
		if(deu instanceof UserProfile) {
			
			UserProfile subProfile = (UserProfile) deu;
			
			List<TunnelProcedure> subTunnels = TunnelProcedure.getTunnels(subProfile.getUser());
		
			TunnelProcedure subTunnel = TunnelProcedure.getTunnelByDeu(subTunnels, oldSource);
			
			if(subTunnel != null) {
				
				subTunnel.redirect(tunnel.getProfile());
				subTunnel.save();
			}
		}
	/*	else if(deu instanceof Room) {
			
			//TODO
		} */
	}
	
	public void onComplete() {
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), newSource, "$procedure.redirect.successful");
	}
	
	private List<TunnelProcedure> getTunnels() {
		
		if(tunnels == null) {
			
			this.tunnels = TunnelProcedure.getTunnels(this.newSource.getUser());
			Collections.sort(this.tunnels, (TunnelProcedure o1, TunnelProcedure o2) -> o1.getId().compareTo(o2.getId()));
		}
		
		return this.tunnels;
	}
	
	public void setNewSource(UserProfile profile) {
		
		this.newSource = profile;
	}
	
	public UserProfile getNewSource() {
		
		return this.newSource;
	}
	
	public static TunnelRedirectProcedure create(UserProfile profile) {
		
		TunnelRedirectProcedure procedure = new TunnelRedirectProcedure(profile);

		procedure = repository.saveAndFlush(procedure);
		
		return procedure;
	}
}
