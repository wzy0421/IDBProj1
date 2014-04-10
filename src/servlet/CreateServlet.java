package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import datatypes.*;
import oracle.jdbc.pool.OracleDataSource;
import services.SQLOperations;

@WebServlet("/CreateServlet")
public class CreateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String connect_string = "jdbc:oracle:thin: hy2379/nmLkTkct@//w4111c.cs.columbia.edu/ADB";
	private Connection conn;

	/**
	 * Default constructor.
	 */
	public CreateServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String organizerFacebookId = request.getParameter("organizerFacebookId");
		String timeString = request.getParameter("timeString");
		String locationString = request.getParameter("locationString");
		String attendeeFidString = request.getParameter("attendeeFidString");
		
		String title = request.getParameter("title");
		String detail = request.getParameter("detail");
		
		List<CreatePlan> plans = new LinkedList<CreatePlan>();
		while (!timeString.isEmpty()) {
			int i = timeString.indexOf('!');
			int j = locationString.indexOf('!');
			CreatePlan plan = new CreatePlan(locationString.substring(0,j),timeString.substring(0, i));
			plans.add(plan);
			timeString = timeString.substring(i+1);
			locationString = locationString.substring(j+1);
		}
		
		List<String> invitees = new LinkedList<String>();
		while(!attendeeFidString.isEmpty()) {
			int k = attendeeFidString.indexOf('!');
			invitees.add(attendeeFidString.substring(0,k));
			attendeeFidString = attendeeFidString.substring(k+1);
		}
		
		CreateEvent event = new CreateEvent(organizerFacebookId,title,detail,plans);
				
			try {
				if (conn == null || conn.isClosed()) {
					// Create a OracleDataSource instance and set URL
					OracleDataSource ods = new OracleDataSource();
					ods.setURL(connect_string);
					conn = ods.getConnection();
					
					SQLOperations sql = new SQLOperations(conn);
					int eventId = sql.postAnEvent(event);
					//TODO: invite
					for(String fid:invitees)
						sql.invite(eventId, fid);
					
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		System.out.println("Organizer: "+organizerFacebookId);
		//System.out.println(plans.toString());

		System.out.println("Title: "+title);
		System.out.println("Detail: "+detail);
		
		
		response.sendRedirect("http://localhost:8080/CS4111Proj1/main?facebookId="+organizerFacebookId);
		
		}

	}
