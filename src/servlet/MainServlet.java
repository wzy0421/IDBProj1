package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import datatypes.*;
import oracle.jdbc.pool.OracleDataSource;
import services.SQLOperations;

@WebServlet("/MainServlet")
public class MainServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String connect_string = "jdbc:oracle:thin: hy2379/nmLkTkct@//w4111c.cs.columbia.edu/ADB";
	private Connection conn;
	static String facebookId;
	static String accessToken;
	static String userName;
	static int eventId;

	/**
	 * Default constructor.
	 */
	public MainServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pw = new PrintWriter(response.getOutputStream());

		facebookId = request.getParameter("facebookId");
		accessToken = request.getParameter("accessToken");

		// Not log in from FB, or not jump from local site
		if (facebookId == null)
			response.sendRedirect("http://localhost:8080/CS4111Proj1/");

		try {
			if (conn == null || conn.isClosed()) {
				// Create a OracleDataSource instance and set URL
				OracleDataSource ods = new OracleDataSource();
				ods.setURL(connect_string);
				conn = ods.getConnection();
			}
			SQLOperations sql = new SQLOperations(conn);

			// Check if user exists
			if (facebookId != null && accessToken != null) {
				User user = new User(facebookId, accessToken);
				user.login();
				userName = user.getName();

				if (!sql.userExists(user.getFacebookId())) {
					System.out.println("User Not Exists! Creating...");
					sql.addNewUser(user.getFacebookId(), user.getName());
				}

			} else { // userFacebookId!=null
				userName = sql.getUserName(facebookId);
			}

			response.setContentType("text/html");

			// -----------------Print the banner-----------------
			printHeader(pw);
			pw.println("<div id=\"fb-root\"></div>");
			pw.println("<li><a href=\"#\" id=\"user\" >" + userName
					+ "</a></li>");
			pw.println("<li><button class=\"btn\" onclick=\"logout()\">Logout</button></li>");
			pw.println("</ul></div></div></div></div>");

			// -----------------Print the nav bar-----------------
			pw.println("<div class=\"container-fluid\">");
			pw.println("<div class=\"row-fluid\">");
			pw.println("<div class=\"span3\">");
			pw.println("<div class=\"well sidebar-nav\">");
			pw.println("<ul class=\"nav nav-list\">");

			// -----------------Organizer Event List-----------------
			pw.println("<li class=\"nav-header\">I'm the Organizer</li>");
			List<Event> orgList = sql.getAllCreatedEvents(facebookId);
			Iterator<Event> itr = orgList.iterator();
			while (itr.hasNext()) {
				Event event = itr.next();
				int determinedPid = sql.isDetermined(event.eid);
				if(determinedPid==-1)
				pw.println("<li><a href=\"http://localhost:8080/CS4111Proj1/decide?eventId="
						+ event.eid
						+ "&facebookId="
						+ facebookId
						+ "\">"
						+ event.ename + "<span class=\"label label-warning\" style=\"float:right\">Decide</span></a></li>");
				else
					pw.println("<li><a href=\"http://localhost:8080/CS4111Proj1/decideresult?eventId="
							+ event.eid
							+ "&facebookId="
							+ facebookId
							+ "&planId="+determinedPid
							+ "\">"
							+ event.ename + "<span class=\"label label-success\" style=\"float:right\">Decided</span></a></li>");
					
			}

			pw.println("<form name=\"userForm\" action=\"createpage.jsp\">");
			pw.println("<input type=\"hidden\" id=\"userName\" name=\"userName\"/>");
			pw.println("<input type=\"hidden\" id=\"facebookId\" name=\"facebookId\"/>");
			pw.println("</form>");
			pw.println("<p><a href=\"#\" class=\"btn btn-primary\" onclick=\"gotoCreate()\">Create a New Event</a></p>");

			// -----------------Attendee Event List-----------------
			List<Event> attList = sql.getAllAttendedEvents(facebookId);
			itr = attList.iterator();
			pw.println("<li class=\"nav-header\">I'm the Attendee</li>");
			while (itr.hasNext()) {
				Event event = itr.next();

				int votePid = sql.hasVoted(facebookId, event.eid);
				if (votePid != -1) {
					int determinedPid=sql.isDetermined(event.eid);
					if (determinedPid!=-1) {
						pw.print("<li><a href=\"http://localhost:8080/CS4111Proj1/decideresult?eventId="
								+ event.eid
								+ "&facebookId="
								+ facebookId
								 + "\">" + event.ename);
						pw.println("<span class=\"label label-success\" style=\"float:right\">Decided</span></a></li>");

					} else {
						pw.print("<li><a href=\"http://localhost:8080/CS4111Proj1/voteresult?eventId="
								+ event.eid
								+ "&facebookId="
								+ facebookId
								+ "&planId=" + votePid + "\">" + event.ename);
						pw.println("<span class=\"label label-default\" style=\"float:right\">Voted</span></a></li>");

					}
				} else {
					pw.print("<li><a href=\"http://localhost:8080/CS4111Proj1/vote?eventId="
							+ event.eid
							+ "&facebookId="
							+ facebookId
							+ "\">"
							+ event.ename);
					pw.println("<span class=\"label label-warning\" style=\"float:right\">Vote</span></a></li>");

				}

			}

			pw.println("</ul>");

			printRest(pw);
			conn.close();

		} catch (SQLException e) {
			pw.println(e.getMessage());
		}

		pw.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private void printHeader(PrintWriter pw) {

		pw.println("<html>");
		pw.println("<head>");
		pw.println("<meta charset=\"utf-8\">");

		pw.println("<title>Plangout</title>");
		pw.println("<link href=\"stylesheets/bootstrap.css\" rel=\"stylesheet\">");
		pw.println("<link href=\"stylesheets/bootstrap-responsive.css\" rel=\"stylesheet\">");

		pw.println("<style type=\"text/css\">");
		pw.println("body {padding-top: 60px;padding-bottom: 40px;}");
		pw.println(".sidebar-nav {padding: 9px 0;}");
		pw.println("</style>");

		pw.println("<script>");
		pw.println("function gotoCreate() {");
		pw.println("document.getElementById('userName').value = \"" + userName
				+ "\";");
		pw.println("document.getElementById('facebookId').value = \""
				+ facebookId + "\";");
		pw.println("document.forms[0].submit();}");

		pw.println("function acceptInvitation(eventId){");
		pw.println("document.getElementById('facebookId').value = \""
				+ facebookId + "\";");
		pw.println("document.getElementById('eventId').value = eventId;");
		pw.println("document.forms[1].submit();}");

		pw.println("window.fbAsyncInit = function() {");
		pw.println("FB.init({");
		pw.println("appId      : '242048779315324', // App ID");
		pw.println("status     : true, // check login status");
		pw.println("cookie     : true, // enable cookies to allow the server to access the session");
		pw.println("xfbml      : true  // parse XFBML");
		pw.println("});};");

		pw.println("(function(d){");
		pw.println("var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];");
		pw.println("if (d.getElementById(id)) {return;}");
		pw.println("js = d.createElement('script'); js.id = id; js.async = true;");
		pw.println("js.src = \"https://connect.facebook.net/en_US/all.js\";");
		pw.println("ref.parentNode.insertBefore(js, ref);");
		pw.println("}(document));");

		pw.println("function logout() {");
		pw.println("FB.logout(function(response) {");
		pw.println("alert('Logged out.... ');});");

		pw.println("window.location.href=\"http://localhost:8080/CS4111Proj1\";}");
		pw.println("</script>");

		pw.println("</head>");
		pw.println("<body>");

		pw.println("<div class=\"navbar navbar-inverse navbar-fixed-top\">");
		pw.println("<div class=\"navbar-inner\">");
		pw.println("<div class=\"container\">");
		pw.println("<button type=\"button\" class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">");
		pw.println("<span class=\"icon-bar\"></span>");
		pw.println("<span class=\"icon-bar\"></span>");
		pw.println("<span class=\"icon-bar\"></span>");
		pw.println("</button>");
		pw.println("<a class=\"brand\" href=\"main\"><span>Plan</span>gout</a>");
		pw.println("<div class=\"nav-collapse collapse\">");
		pw.println("<ul class=\"nav pull-right\">");

	}

	private void printRest(PrintWriter pw) {
		pw.println("</div><!--/.well -->");
		pw.println("</div><!--/span3-->");

		pw.println("<div class=\"span9\">");
		pw.println("<div class=\"hero-unit\">");
		pw.println("<h2>Welcom to Plangout!</h2>");
		pw.println("<p>Here you can create events and invite your friends.</p>");
		pw.println("<a name=\"organizer1\">here I am!</a>");

		pw.println("</div><!--/.hero -->");
		pw.println("</div><!--/span9-->");
		pw.println("</div><!--/row-->");
		pw.println("</div><!--fluid-->");

		pw.println("<hr>");

		pw.println("<footer style=\"text-align: center\">");
		pw.println("<p>&copy; Columbia University 2014</p>");
		pw.println("</footer>");

		pw.println("</body>");
		pw.println("</html>");
	}
}
