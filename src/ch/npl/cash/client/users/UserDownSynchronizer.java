package ch.npl.cash.client.users;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import ch.npl.cash.client.users.UserProvider.User;
import ch.npl.cash.domain.UserMoney;

public class UserDownSynchronizer {
	private UserProvider userSource;
	private EntityManager emTarget;

	public UserDownSynchronizer(UserProvider source, EntityManager target) {
		this.userSource = source;
		this.emTarget = target;
	}
	
	public void syncronize() {
		List<User> users = userSource.fetchUsers();
		saveOnTarget(users);
		System.out.println("[UserDownSynchronizer] " + users.size() + " Users down-synced.");
	}
	
	private void saveOnTarget(List<User> users) {
		// TODO mein Gefühl warnt mich. Ginge es nicht auch ohne diese starke Abhängigkeit von UserMoney
		TypedQuery<UserMoney> q = emTarget.createQuery("select u from UserMoney u", UserMoney.class);
		List<UserMoney> l = q.getResultList();
		for (UserMoney m : l) { // TODO unschön, aber muss jetzt schnell gehen
			for (Iterator<User> iter = users.iterator(); iter.hasNext();) {
				User u = iter.next();
				if (u.getId() == m.getId()) {
					iter.remove(); // entferne alle schon existierenden User aus der Syncliste
				}
			}
		}
		
		// Speichere die übrig gebliebenen User als neue User in der Kassendatenbank
		emTarget.getTransaction().begin();
		for (User u : users) {
			UserMoney m = new UserMoney();
			m.setId(u.getId());
			m.setUsername(u.getUsername());
			m.setBalance(0);
			emTarget.persist(m);
		}
		emTarget.getTransaction().commit();
	}
}
