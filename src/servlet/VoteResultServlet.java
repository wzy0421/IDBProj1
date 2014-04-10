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

@WebServlet("/VoteResultServlet")
public class VoteResultServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String connect_string = "jdbc:oracle:thin: hy2379/nmLkTkct@//w4111c.cs.columbia.edu/ADB";
	private Connection conn;

	static String userName;
	static String facebookId;
	static String eventId;
	static String planId;
	static int eId;
	static int pId;

	/**
	 * Default constructor.
	 */
	public VoteResultServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter pw = new PrintWriter(response.getOutputStream());

		eventId = request.getParameter("eventId");
		facebookId = request.getParameter("facebookId");
		planId = request.getParameter("planId");

		// Not log in from FB, or not jump from local site
		if (eventId == null || facebookId == null || planId == null) {
			System.out.println("null param");
			response.sendRedirect("http://localhost:8080/CS4111Proj1/");
		} else {
			eId = Integer.valueOf(eventId);
			pId = Integer.valueOf(planId);
		}

		try {
			if (conn == null || conn.isClosed()) {
				// Create a OracleDataSource instance and set URL
				OracleDataSource ods = new OracleDataSource();
				ods.setURL(connect_string);
				conn = ods.getConnection();
			}
			SQLOperations sql = new SQLOperations(conn);
			sql.vote(facebookId, eId, pId);
			userName = sql.getUserName(facebookId);
			Event event = sql.getEvent(eId);

			List<Plan> planList = sql.getAllPlans(eId);

			response.setContentType("text/html");

			// -----------------Print the Header-----------------
			printHeader(pw);

			pw.println("function addMarkers() {");
			for (int i = 0; i < planList.size(); i++) {
				Plan p = planList.get(i);
				String location = p.location;
				int i_comma = location.indexOf(',');
				String Lat = location.substring(0, i_comma);
				int i_colon = location.indexOf(':');
				String Lng = location.substring(i_comma + 1, i_colon - 1);
				String address = location.substring(i_colon + 1);

				pw.println("latLng = new google.maps.LatLng(" + Lat + "," + Lng
						+ ");");
				pw.println("marker = new google.maps.Marker({");
				pw.println("position : latLng,");
				pw.println("map : map});");
				pw.println("markers.push(marker);");
			}
			pw.println("}");

			// -----------------Print the Banner-----------------
			pw.println("function toggleBounce(ind) {");
			pw.println("if (markers[ind].getAnimation() != null) {");
			pw.println("markers[ind].setAnimation(null);");
			pw.println("} else markers[ind].setAnimation(google.maps.Animation.BOUNCE);}");

			pw.println("google.maps.event.addDomListener(window, 'load', initialize);");
			pw.println("</script></head>");

			pw.println("<body>");
			pw.println("<div class=\"navbar navbar-inverse navbar-fixed-top\">");
			pw.println("<div class=\"navbar-inner\">");
			pw.println("<div class=\"container\">");
			pw.println("<button type=\"button\" class=\"btn btn-navbar\" data-toggle=\"collapse\" data-target=\".nav-collapse\">");
			pw.println("<span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span> <span class=\"icon-bar\"></span>");
			pw.println("</button>");
			pw.println("<a class=\"brand\" href=\"#\"><span>Plan</span>gout</a>");
			pw.println("<div class=\"nav-collapse collapse\">");
			pw.println("<ul class=\"nav\"></ul>");
			pw.println("<ul class=\"nav pull-right\">");
			pw.println("<div id=\"fb-root\"></div>");
			pw.println("<li><a href=\"#\" id=\"user\" >" + userName
					+ "</a></li>");
			pw.println("<li><button class=\"btn\" onclick=\"logout()\">Logout</button></li>");
			pw.println("</ul></div></div></div></div>");

			// -----------------Print the Title-----------------
			pw.println("<div class=\"container-fluid\">");
			pw.println("<div class=\"hero-unit\" >");
			pw.println("<h1>Voting Result Of " + event.ename + "</h1>");
			pw.println("<p id=\"content\" name=\"eventcontent\">"
					+ event.detail + "</p>");
			pw.println("</div>");

			// -----------------Print the Plans-----------------
			pw.println("<div class=\"row-fluid\">");
			pw.println("<div class=\"span4\">");
			pw.println("<form id=\"return\" name=\"returnform\" action=\"main\">");

			pw.println("<div class=\"well sidebar-nav\">");
			pw.println("<ul class=\"nav nav-list\">");
			pw.println("<li class=\"nav-header\">Result of Voting</li>");

			for (int i = 0; i < planList.size(); i++) {
				Plan p = planList.get(i);
				String location = p.location;
				String time = p.activity_time;
				int i_comma = location.indexOf(',');
				String Lat = location.substring(0, i_comma);
				int i_colon = location.indexOf(':');
				String Lng = location.substring(i_comma + 1, i_colon - 1);
				String address = location.substring(i_colon + 1);
				pw.print("<li><a onmouseover=\"toggleBounce(" + i
						+ ")\" onmouseout=\"toggleBounce(" + i
						+ ")\"><label><span class=\"badge\">" + p.vote
						+ "</span>");
				if (pId == p.pid)
					pw.println("<font color=\"000000\">"  + time+" : "+ address
							+ "</font></label></a></li>");
				else
					pw.println( time+" : "+address + "</label></a></li>");
			}
			pw.println("</ul>");
			pw.println("</div>");
			pw.println("<input type=\"hidden\" name=\"facebookId\" value=\'"
					+ facebookId + "\' />");

			printRest(pw);
			conn.close();

		} catch (SQLException e) {
			pw.println(e.getMessage());
		}

		pw.close();

		// TODO Auto-generated method stub
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

		pw.println("<html><head>");
		pw.println("<meta charset=\"utf-8\">");
		pw.println("<title>Plangout | Voting result of event</title>");
		pw.println("<link href=\"stylesheets/bootstrap.css\" rel=\"stylesheet\">");
		pw.println("<link href=\"stylesheets/bootstrap-responsive.css\" rel=\"stylesheet\">");
		pw.println("<link href=\"stylesheets/main.css\" rel=\"stylesheet\">");
		pw.println("<link rel=\"shortcut icon\" href=\"images/favicon.png\">");

		pw.println("<script type=\"text/javascript\" src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyCmkkqJj389oWXZq1PgwiXix4aHjqwyjMs&sensor=true\"></script>");

		pw.println("<style type=\"text/css\">");
		pw.println("body {padding-top: 60px; padding-bottom: 40px;}");
		pw.println("#google-map-canvas {height: 400px; width: 660px;}");
		pw.println("</style>");
		pw.println("<script>");

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

		pw.println("<script type=\"text/javascript\">");
		pw.println("var map;");
		pw.println("var markers = [];");

		pw.println("function initialize() {");
		pw.println("var latLng = new google.maps.LatLng(40.80754, -73.96257);");
		pw.println("var mapOptions = {");
		pw.println("zoom : 12,");
		pw.println("center : latLng,");
		pw.println("mapTypeId : google.maps.MapTypeId.ROADMAP};");
		pw.println("map = new google.maps.Map(document.getElementById(\"google-map-canvas\"), mapOptions);");
		pw.println("addMarkers();}");

	}

	private void printRest(PrintWriter pw) {
		pw.println("</form>");
		pw.println("<div align=\"right\">");
		pw.println("<button id=\"rerbtn\" class=\"btn btn-primary\" onclick=\"gotomain()\">Return</button>");
		pw.println("</div>");
		pw.println("<script>");
		pw.println("function gotomain() {");
		pw.println("document.forms[0].submit();}");
		pw.println("</script>");
		pw.println("</div>");
		pw.println("<div class=\"span8\">");
		pw.println("<div id=\"google-map-canvas\" style=\"width: 100%\"></div>");
		pw.println("</div>");
		pw.println("</div>");
		pw.println("</div>");

		pw.println("<hr>");
		pw.println("<footer style=\"text-align: center\">");
		pw.println("<p>&copy; Columbia University 2014</p>");
		pw.println("</footer>");

		pw.println("<script src=\"javascripts/jquery.js\"></script>");
		pw.println("<script src=\"javascripts/bootstrap.js\" type=\"text/javascript\"></script>");
		pw.println("</body>");
		pw.println("</html>");
	}
}
