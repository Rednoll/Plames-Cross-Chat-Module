package com.inwaiders.plames.modules.crosschat.domain.room.additions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.ManyToMany;

import com.inwaiders.plames.modules.crosschat.domain.room.Room;
import com.inwaiders.plames.modules.crosschat.domain.room.impl.RoomImpl;

@Embeddable
public class RoomsContainerImpl implements RoomsContainer{

	@ManyToMany(cascade = CascadeType.MERGE, targetEntity = RoomImpl.class, mappedBy = "members.users")
	private Set<Room> rooms = new HashSet<Room>();
	
	public Room getByName(String name) {
	
		for(Room room : rooms) {
			
			if(room.getName().equals(name)) {
				
				return room;
			}
		}
		
		return null;
	}
	
	public void setRooms(Set<Room> rooms) {
		
		this.rooms = rooms;
	}

	public Set<Room> getRooms() {
		
		return this.rooms;
	}

	@Override
	public boolean add(Room e) {
	
		return rooms.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends Room> c) {

		return rooms.addAll(c);
	}

	@Override
	public void clear() {
	
		rooms.clear();
	}

	@Override
	public boolean contains(Object o) {
		
		return rooms.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		
		return rooms.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		
		return rooms.isEmpty();
	}

	@Override
	public Iterator<Room> iterator() {
		
		return rooms.iterator();
	}

	@Override
	public boolean remove(Object o) {
		
		return rooms.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		
		return rooms.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		
		return rooms.removeAll(c);
	}

	@Override
	public int size() {
		
		return rooms.size();
	}

	@Override
	public Object[] toArray() {
		
		return rooms.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		
		return rooms.toArray(a);
	}
}
