package com.inwaiders.plames.modules.crosschat.domain.room;

import java.util.Set;

import com.inwaiders.plames.api.messenger.message.MessageReceiver;
import com.inwaiders.plames.api.messenger.message.MessageSender;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.api.user.additions.UsersContainer;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;

public interface Room extends MessageReceiver, MessageSender {

	public DescribedFunctionResult op(User user, User opper);
	public DescribedFunctionResult deop(User user, User deopper);
	public DescribedFunctionResult kick(User user, User kicker);
	
	public DescribedFunctionResult inviteAccepted(User user);
	public DescribedFunctionResult invite(User user, User inviter);
	
	public void joinUser(User user);
	public void leaveUser(User user);
	
	public void setName(String name);
	public String getName();
	
	public void setCreator(User user);
	public User getCreator();
	
	public UsersContainer getAdminsContainer();
	public Set<User> getAdmins();
	
	public UsersContainer getInvitedContainer();
	public Set<User> getInvited();
	
	public UsersContainer getMembersContainer();
	public Set<User> getMembers();
	
	public void introduceUser(User user);
	
	public void setIntroduceMessage(String message);
	public String getIntroduceMessage();
	
	public void save();
	public void delete();
}
