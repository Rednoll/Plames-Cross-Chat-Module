package com.inwaiders.plames.modules.crosschat.domain.room.impl;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.inwaiders.plames.api.messenger.MessengerException;
import com.inwaiders.plames.api.messenger.message.Message;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.messenger.profile.additions.ProfilesContainer;
import com.inwaiders.plames.api.procedure.Procedure;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.api.user.additions.UsersContainer;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.api.utils.DescribedFunctionResult.Status;
import com.inwaiders.plames.domain.user.impl.UserImpl;
import com.inwaiders.plames.domain.user.impl.additions.UsersContainerImpl;
import com.inwaiders.plames.modules.crosschat.CrossChatModule;
import com.inwaiders.plames.modules.crosschat.dao.room.RoomRepository;
import com.inwaiders.plames.modules.crosschat.domain.procedure.TunnelProcedure;
import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.modules.crosschat.domain.room.message.RoomMessageStub;
import com.inwaiders.plames.system.utils.MessageUtils;

@Entity(name = "Room")
@Table(name = "rooms")
@Inheritance(strategy = InheritanceType.JOINED)
public class RoomImpl implements Room {

	private static transient RoomRepository repository;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@JoinColumn(name = "creator_id")
	@ManyToOne(targetEntity = UserImpl.class)
	private User creator = null;
	
	@Embedded
	@AssociationOverride(name = "users", joinTable = @JoinTable(name = "admins_rooms_mtm", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "user_id")))
	private UsersContainerImpl admins = new UsersContainerImpl();
	
	@Embedded
	@AssociationOverride(name = "users", joinTable = @JoinTable(name = "users_rooms_mtm", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "user_id")))
	private UsersContainerImpl members = new UsersContainerImpl();
	
	@Embedded
	@AssociationOverride(name = "users", joinTable = @JoinTable(name = "invited_users_rooms_mtm", joinColumns = @JoinColumn(name = "room_id"), inverseJoinColumns = @JoinColumn(name = "user_id")))
	private UsersContainerImpl invited = new UsersContainerImpl();
	
	@Column(name = "name")
	private String name = null;	
	
	@Column(name = "introduce_message")
	private String introduceMessage = "Добро пожаловать!";
	
	@Column(name = "admins_immunity")
	private boolean adminsImmunity = true;
	
	@Column(name = "invite_free")
	private boolean inviteFree = true;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;

	public int hashCode() {

		return Objects.hash(getId());
	}

	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomImpl other = (RoomImpl) obj;
		if (admins == null) {
			if (other.admins != null)
				return false;
		} else if (!admins.equals(other.admins))
			return false;
		if (adminsImmunity != other.adminsImmunity)
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (introduceMessage == null) {
			if (other.introduceMessage != null)
				return false;
		} else if (!introduceMessage.equals(other.introduceMessage))
			return false;
		if (inviteFree != other.inviteFree)
			return false;
		if (invited == null) {
			if (other.invited != null)
				return false;
		} else if (!invited.equals(other.invited))
			return false;
		if (members == null) {
			if (other.members != null)
				return false;
		} else if (!members.equals(other.members))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public DescribedFunctionResult deop(User user, User deopper) {
		
		if(!admins.contains(deopper)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.rights_nf");
		if(!admins.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.not_admin", user.getNickname());
		
		if(creator.getId() == user.getId()) return new DescribedFunctionResult(Status.ERROR, "$room.domain.creator_immunity");
		
		admins.remove(user);
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), this, "$room.domain.deop.success", deopper.getNickname(), user.getNickname());
		
		save();
		
		return DescribedFunctionResult.OK;
	}
	
	@Override
	public DescribedFunctionResult op(User user, User opper) {
		
		if(!admins.contains(opper)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.rights_nf");
		if(admins.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.already_admin", user.getNickname());
		
		admins.add(user);
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), this, "$room.domain.op.success", opper.getNickname(), user.getNickname());
		
		save();
		
		return DescribedFunctionResult.OK;
	}
	
	@Override
	public DescribedFunctionResult kick(User user, User kicker) {
		
		if(creator.getId() == user.getId()) return new DescribedFunctionResult(Status.ERROR, "$room.domain.creator_immunity");
		
		if(adminsImmunity) {
			
			if(admins.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.admin_immunity");
		}
		
		if(!admins.contains(kicker)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.rights_nf");
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), this, "$room.domain.kick.success", kicker.getNickname(), user.getNickname());
		
		leaveUser(user);
		
		return DescribedFunctionResult.OK;
	}
	
	@Override
	public DescribedFunctionResult inviteAccepted(User user) {
		
		if(!invited.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.invite_accept.not_invited", getName());
		
		invited.remove(user);
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), this, "$room.domain.invite_accept.success.invite_info", user.getNickname());
		
		joinUser(user);
		
		return DescribedFunctionResult.OK;
	}
	
	@Override
	public DescribedFunctionResult invite(User user, User inviter) {
		
		if(!inviteFree) {
		
			if(!admins.contains(inviter)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.rights_nf");
		}
		
		if(members.contains(user)) return new DescribedFunctionResult(Status.ERROR, "$room.domain.invite.already_er", user.getNickname());
		
		UserProfile profile = user.getProfilesContainer().pickProfile();
	
		invited.add(user);	
		MessageUtils.send(CrossChatModule.getSystemProfile(), profile, "$room.domain.invite.success_sub", inviter.getNickname(), getName(), getName());
		
		save();
		
		return DescribedFunctionResult.OK;
	}
	
	public void joinUser(User user) {
		
		this.members.add(user);
		save();
	}
	
	public void leaveUser(User user) {
		
		this.members.remove(user);
		save();
	}
	
	@Override
	public void introduceUser(User user) {
		
		UserProfile profile = getTargetProfile(user);
		
		MessageUtils.send(CrossChatModule.getSystemProfile(), profile, getIntroduceMessage());
	}
	
	@Override
	public boolean receiveMessage(Message message) throws MessengerException {
		
		String text = message.getText();
		
		Set<UserProfile> profiles = collectProfiles();
		
		for(UserProfile profile : profiles) {
			
			Message stub = null;
			
			if(message.getSender() instanceof UserProfile) {
				
				if(profile.getUser().getId() == ((UserProfile) message.getSender()).getUser().getId()) continue;	
			
				stub = new RoomMessageStub(this, profile, message);
			}
			else {
				
				stub = new RoomMessageStub(this, profile, message);
			}
			
			stub.send();
		}
		
		return true;
	}
	
	protected Set<UserProfile> collectProfiles() {
		
		Set<UserProfile> result = new HashSet<>();
		
		for(User user : members) {

			UserProfile profile = getTargetProfile(user);
		
			result.add(profile);
		}
		
		return result;
	}
	
	protected UserProfile getTargetProfile(User user) {
		
		ProfilesContainer profilesContainer = user.getProfilesContainer();
	
		Set<UserProfile> profiles = profilesContainer.getProfiles();
	
		for(UserProfile profile : profiles) {
			
			if(profile.isOnline()) {

				Procedure proc = profile.getCurrentProcedure();
			
				if(proc instanceof TunnelProcedure) {
					
					TunnelProcedure tunnel = (TunnelProcedure) proc;
					
					if(tunnel.getTargetDeu().getId() == this.getId()) {
	
						return profile;
					}
				}
			}
		}
		
		return profilesContainer.pickProfile();
	}
	
	public void setAdminsImmunity(boolean immunity) {
		
		this.adminsImmunity = immunity;
	}
	
	public boolean getAdminsImmunity() {
		
		return this.adminsImmunity;
	}
	
	public void setIntroduceMessage(String message) {
		
		this.introduceMessage = message;
	}
	
	public String getIntroduceMessage() {
		
		return this.introduceMessage;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return name;
	}
	
	public void setCreator(User creator) {
		
		this.creator = creator;
		this.admins.add(creator);
	}
	
	public User getCreator() {
		
		return this.creator;
	}
	
	public UsersContainer getAdminsContainer() {
		
		return this.admins;
	}
	
	public Set<User> getAdmins() {
	
		return this.admins;
	}
	
	public UsersContainer getMembersContainer() {
		
		return this.members;
	}

	public Set<User> getMembers() {
		
		return this.members;
	}
	
	public UsersContainer getInvitedContainer() {
		
		return this.members;
	}
	
	public Set<User> getInvited() {
		
		return this.invited;
	}
	
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}
	
	public void delete() {
		
		deleted = true;
		repository.save(this);
	}
	
	public static RoomImpl create() {
		
		RoomImpl room = new RoomImpl();
	
		room = repository.save(room);
		
		return room;
	}
	
	public static RoomImpl getByName(String name) {
		
		return repository.getByName(name);
	}
	
	public static RoomImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static void setRepository(RoomRepository rep) {
		
		repository = rep;
	}
	
	public Long getId() {
		
		return this.id;
	}
}
