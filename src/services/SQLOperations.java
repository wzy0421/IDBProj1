package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import datatypes.*;

// Operations on Database
public class SQLOperations {

	private Connection conn;

	public SQLOperations(Connection conn) {
		this.conn = conn;
	}

	/**
	 * Add a new user to users table
	 * 
	 * @param facebookId
	 * @param username
	 * @return true if succeeds, else false
	 */
	public boolean addNewUser(String facebookId, String username) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO users VALUES('" + facebookId
					+ "','" + username + "')");

			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Check if user exists in users table
	 * 
	 * @param facebookId
	 * @return true if exists, else false
	 */
	public boolean userExists(String facebookId) {
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("select * from users where facebookId=\'"
							+ facebookId + "\'");
			
			if (rset.next()) {
				rset.close();
				stmt.close();
				return true;
			} else {
				rset.close();
				stmt.close();
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get user's name from users table
	 * 
	 * @param facebookId
	 * @return user's name
	 */
	public String getUserName(String facebookId) {
		String uname = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("select * from users where facebookId=\'"
							+ facebookId + "\'");

			while (rset.next()) {
				uname = rset.getString("uname");
			}
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uname;
	}

	/**
	 * Create an event, update events table and plans table
	 * 
	 * @param event
	 * @return the eid of the inserted record, if insertion fails, return 0
	 */
	public int postAnEvent(CreateEvent event) {
		try {
			String[] col_names = { "eid" };
			Statement s = conn.createStatement();
			s.execute("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'");
			s.close();
			String sql = "INSERT INTO events VALUES('" + event.facebookId
					+ "',0,'" + event.ename + "','" + event.detail.replaceAll("'", "''") + "')";
			PreparedStatement stmt = conn.prepareStatement(sql, col_names);
			stmt.executeUpdate();
			ResultSet rset = stmt.getGeneratedKeys();
			rset.next();
			int eid = rset.getInt(1);
			for (CreatePlan plan : event.plans) {
				String sql_plan = "INSERT INTO plans VALUES(" + eid + "," + 0
						+ ",'" + plan.location + "', '" + plan.activity_time
						+ "')";
				System.out.println(sql_plan);
				Statement stmt_plan = conn.createStatement();
				stmt_plan.executeUpdate(sql_plan);
			}
			rset.close();
			stmt.close();
			return eid;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public Event getEvent(int eid) {
		Event event = new Event();

		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("select * from events where eid="
							+ eid);

			while (rset.next()) {
				event.eid = eid;
				event.facebookId=rset.getString("facebookId");
				event.ename = rset.getString("ename");
				event.detail = rset.getString("detail");
			}
			rset.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return event;
		
	}

	/**
	 * Get all created events by the user
	 * 
	 * @param facebookId
	 * @return list of created events
	 */
	public List<Event> getAllCreatedEvents(String facebookId) {

		List<Event> result = new LinkedList<Event>();
		try {
			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("SELECT * FROM events WHERE facebookId='"
							+ facebookId + "'");
			while (rset.next()) {
				Event current_event = new Event();
				current_event.facebookId = rset.getString("facebookId");
				current_event.ename = rset.getString("ename");
				current_event.detail = rset.getString("detail");
				current_event.eid = rset.getInt("eid");
				result.add(current_event);
			}
			rset.close();
			stmt.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		}
	}

	/**
	 * Get all attended events by the user
	 * 
	 * @param facebookId
	 * @return list of attended events
	 */
	public List<Event> getAllAttendedEvents(String facebookId) {
		List<Event> result = new LinkedList<Event>();
		try {
			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("SELECT UNIQUE eid FROM invitations where facebookId='"
							+ facebookId + "'");
			while (rset.next()) {
				stmt = conn.createStatement();
				int eid = rset.getInt("eid");
				ResultSet rset_event = stmt
						.executeQuery("SELECT * FROM events where eid=" + eid);
				while (rset_event.next()) {
					Event current_event = new Event();
					current_event.facebookId = rset_event
							.getString("facebookId");
					current_event.ename = rset_event.getString("ename");
					current_event.detail = rset_event.getString("detail");
					current_event.eid = rset_event.getInt("eid");
					result.add(current_event);
				}
			}
			rset.close();
			stmt.close();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			return result;
		}
	}

	/**
	 * Vote for a plan of an event
	 * 
	 * @param facebookId
	 * @param eid
	 * @param pid
	 * @return true if succeeds, else false
	 */
	public boolean vote(String facebookId, int eid, int pid) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO votes VALUES(" + eid + ",\'"
					+ facebookId + "\'," + pid + ")");

			stmt.close();
			return true;

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get the plan pid the user has voted for
	 * 
	 * @param facebookId
	 * @param eid
	 * @return pid if the user has voted, else -1
	 */
	public int hasVoted(String facebookId, int eid) {
		int pid = -1;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("select * from votes where facebookId='"
							+ facebookId + "' and eid=" + eid);
			while (rset.next()) {
				pid = rset.getInt("pid");
			}
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pid;
	}

	/**
	 * Get all plans of an event
	 * 
	 * @param eid
	 * @return list of plans of an event
	 */
	public List<Plan> getAllPlans(int eid) {
		List<Plan> result = new LinkedList<Plan>();
		try {
			Statement s = conn.createStatement();
			s.execute("ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI'");
			s.close();

			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rset = stmt.executeQuery("SELECT * FROM plans WHERE eid="
					+ eid );
			while (rset.next()) {

				Plan current_plan = new Plan();
				current_plan.pid = rset.getInt("pid");
				current_plan.location = rset.getString("location");
				current_plan.activity_time = rset.getString("activity_time");
				stmt = conn.createStatement();
				ResultSet rset_votes = stmt
						.executeQuery("SELECT count(*) AS cnt FROM votes WHERE eid="
								+ eid + " AND pid=" + current_plan.pid);
				rset_votes.next();
				current_plan.vote = rset_votes.getInt("cnt");
				result.add(current_plan);
			}
			rset.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Determine the final plan of an event
	 * 
	 * @param eid
	 * @param pid
	 * @return true if succeeds, else false
	 */
	public boolean determineAnEvent(int eid, int pid) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO determined_events VALUES(" + eid
					+ "," + pid + ")");

			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Return if an event is determined
	 * 
	 * @param eid
	 * @return pid if is determined, else -1
	 */
	public int isDetermined(int eid) {
		int result = -1;
		try {
			Statement stmt;
			stmt = conn.createStatement();
			ResultSet rset = stmt
					.executeQuery("SELECT * FROM determined_events WHERE eid="
							+ eid );
			while(rset.next())
				result = rset.getInt("pid");
			rset.close();
			stmt.close();
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Invite someone to vote for an event
	 * 
	 * @param eid
	 * @param facebookId
	 * @return true if succeed
	 */
	public boolean invite(int eid, String facebookId) {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO invitations VALUES('" + eid + "','"
					+ facebookId + "')");

			stmt.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Get all invitations of the user
	 * 
	 * @param facebookId
	 * @return list of invitations
	 */
	public List<Event> getAllInvitations(String facebookId) {
		List<Event> result = new LinkedList<Event>();
		try {
			Statement stmt = conn.createStatement();

			ResultSet rset = stmt
					.executeQuery("SELECT e.facebookId,e.eid,e.ename,e.detail FROM invitations i,events e WHERE i.facebookId='"
							+ facebookId + "' AND i.eid=e.eid");

			while (rset.next()) {
				Event current_event = new Event();
				current_event.facebookId = rset.getString("facebookId");
				current_event.ename = rset.getString("ename");
				current_event.detail = rset.getString("detail");
				current_event.eid = rset.getInt("eid");
				result.add(current_event);
			}
			rset.close();
			stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
