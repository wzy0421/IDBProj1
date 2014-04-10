<%@page contentType="text/html;charset=GB2312" %> 
<%@page import="java.util.*" %> 

<html>
    <head>
        
        <link href="stylesheets/bootstrap.css" rel="stylesheet">
        <link href="stylesheets/bootstrap-responsive.css" rel="stylesheet">
		<link href="stylesheets/bootstrap-datetimepicker.min.css" rel="stylesheet">
		<link rel="shortcut icon" href="images/favicon.png">
		<script src="javascripts/jquery.js"></script>
        <script src="javascripts/bootstrap.js" type="text/javascript"></script>
        <script src="javascripts/bootstrap-datetimepicker.min.js" type="text/javascript"></script>
        <script type="text/javascript"
            src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCmkkqJj389oWXZq1PgwiXix4aHjqwyjMs&sensor=true">
        </script>
        <style type="text/css">
            html { height: 100% }
            body {height: 100%; margin: 0; padding: 0; padding-top: 40px; padding-bottom: 40px; background-color: #f5f5f5;}
			#google-map-canvas { height:400px;}
            .form-signin { max-width: 300px; padding: 19px 29px 29px; margin: 0 auto 20px; background-color: #fff;
                border: 1px solid #e5e5e5; -webkit-border-radius: 5px; -moz-border-radius: 5px; border-radius: 5px;
                -webkit-box-shadow: 0 1px 2px rgba(0,0,0,.05); -moz-box-shadow: 0 1px 2px rgba(0,0,0,.05);
                box-shadow: 0 1px 2px rgba(0,0,0,.05); }
            .form-signin .form-signin-heading,
            .form-signin .checkbox { margin-bottom: 10px; }
            .form-signin input[type="text"],
            .form-signin input[type="password"] { font-size: 16px; height: auto; margin-bottom: 15px; padding: 7px 9px; }
            .hero-unit{padding: 30px; font-size: 16px;}
        </style>

        <script type="text/javascript">
            var map;
            markers = [];
            marker_num = 0;
            var infoWindow = new google.maps.InfoWindow;
            var geocoder = new google.maps.Geocoder();
            var address_name = 'I don\'t know!';
            times = [];
    
            locations = [];
            locationnum = 0;
            timenum = 0;
            function getAddressName(latLng) {
                geocoder.geocode({latLng: latLng}, function(responses) {
                                 if (responses && responses.length > 0) {
                                    address_name = responses[0].formatted_address; }
                                 else { address_name = 'Cannot determine the address temporarily...';}});}
        
            function setAddressName(latLng) {
                geocoder.geocode({latLng: latLng}, function(responses) {
                                 if (responses && responses.length > 0) {
                                    address_name = responses[0].formatted_address;
                                    document.getElementById('address').innerHTML += '<li id=\"marker'+locationnum+'\">' + address_name + '</li>';
                                    var tmp =  latLng.lat() + ',' + latLng.lng() + ' : ' + address_name;
                                    locations.push(tmp);
                                    locationnum += 1;}
                                 else {
                                    address_name = 'Cannot determine address at this location temporarily...';
									document.getElementById('address').innerHTML += '<li id=\"marker'+locationnum+'\">' + address_name + '</li>';
                                 
                                     var tmp =  latLng.lat() + ',' + latLng.lng() + ' : ' + address_name;
                                    locations.push(tmp);
                                    locationnum += 1; } }); }
        
            function initialize() {
                var latLng = new google.maps.LatLng(40.80754,-73.96257);
                var mapOptions = {
                    zoom: 12,
                    center: latLng,
                    mapTypeId: google.maps.MapTypeId.ROADMAP };
                
                map = new google.maps.Map(document.getElementById("google-map-canvas"), mapOptions);

                google.maps.event.addListener(map, 'click', function(event) { addMarker(event.latLng); }); }
        
            var updateMarkerWindow = function() {
                var marker = this;
                var latLng = marker.getPosition();
                getAddressName(latLng);
                infoWindow.setContent('<h4>Here is:</h4>' + /*latLng.lat() + ', ' + latLng.lng()+*/ ' <p><b>'+address_name+'</b></p>');
                infoWindow.open(map, marker); };
				
            var mouseOutMarker = function() { infoWindow.close(); };
            
            var clickMarker = function() {
                for (var i = 0; i < locationnum; i++) {
                    if (markers[i] == this) {
                        delete markers[i];
                        this.setMap(null);
                        delete locations[i];
                        document.getElementById("marker" + i).style.display="none";
                        break; } } }
        
            function addMarker(latLng) {
                var marker = new google.maps.Marker({
                    position: latLng,
                    map: map });
                markers.push(marker);
                marker_num++;

                setAddressName(latLng);
                google.maps.event.addListener(marker, 'mouseover', updateMarkerWindow);
                google.maps.event.addListener(marker, 'mouseout', mouseOutMarker);
                google.maps.event.addListener(marker, 'click', clickMarker); }
       
            google.maps.event.addDomListener(window, 'load', initialize);
            
             window.fbAsyncInit = function() { FB.init({
                            appId      : '242048779315324', // App ID
                            status     : true, // check login status
                            cookie     : true, // enable cookies to allow the server to access the session
                            xfbml      : true  // parse XFBML
                            }); };
            
            (function(d){
             var js, id = 'facebook-jssdk', ref = d.getElementsByTagName('script')[0];
             if (d.getElementById(id)) {return;}
             js = d.createElement('script'); js.id = id; js.async = true;
             js.src = "https://connect.facebook.net/en_US/all.js";
             ref.parentNode.insertBefore(js, ref);
             }(document));                   
			
			function logout() {FB.logout(function(response) {
				alert('Logged out.... ');});
				window.location.href="http://localhost:8080/CS4111Proj1";}
				
             function fbCallback(response) {console.log(response);
             var userIDs=response.to;
             var len = userIDs.length;
             for(var i=0; i<userIDs.length;i++)
             	document.getElementById('attendeeFidString').value += userIDs[i]+"!";
             document.forms[0].submit();}
			
			function deleteTime(timex) {
			    document.getElementById('time' + timex).style.display = "none";
			    delete times[timex]; }

            function invite() {
                if (document.getElementById('title').value==null || document.getElementById('title').value=="") {
                    alert("Title cannot be empty!");
                    return; }
                if (document.getElementById('title').length>255) {
                    alert("Too long title!"); }
                if (document.getElementById('detail').value==null || document.getElementById('detail').value=="") {
                    alert("Detail cannot be empty!");
                    return; } 
                document.getElementById('locationString').value = "";
                for (var i=0; i < locationnum; i=i+1) {
                    if (typeof locations[i] != 'undefined') {
                        document.getElementById('locationString').value += locations[i] + "!"; }}

                if (document.getElementById('locationString').value==null || document.getElementById('locationString').value=="") {
                    alert("Locations cannot be empty!");
                    return; } 
                document.getElementById('timeString').value = "";
                for (var i=0; i < timenum; i=i+1) {
                    if (typeof times[i] != 'undefined') {
                        document.getElementById('timeString').value += times[i] + "!"; } }
                if (document.getElementById('timeString').value==null || document.getElementById('timeString').value=="") {
                    alert("Times cannot be empty!"); return;}
                
				if (timenum!=locationnum) {
					alert("The numbers of times and locations don't match!");
					return;
					}
					
             	var myDate = new Date();

             	document.getElementById('organizerFacebookId').value = "<%=request.getParameter("facebookId") %>";
                                         
             	var event_name = document.getElementById('title').value;
             	FB.ui({method:'apprequests',
                       title: event_name,
                       message: 'I just started an event. Join me and vote for it!',
                       }, fbCallback); }
				
				
        </script>

        <meta charset="utf-8">
            <title>Plangout | Create New Event</title>
   </head>
    
    <body>
        <div class="navbar navbar-inverse navbar-fixed-top">
            <div class="navbar-inner">
                <div class="container">
                    <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                        <span class="icon-bar"></span>
                    </button>
                    <a class="brand" href="#">Plangout</a>
                    <div class="nav-collapse collapse">
                        <ul class="nav pull-right">
                            <li><a href="#" id="user" ><%=request.getParameter("userName") %></a></li>
                            <li><button class="btn" onclick="logout()">Logout</button></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
        
        <hr>
        
        <div class="container">
            <form class="form-create" name="eventForm" action="create" method="post">
            	<input type="hidden" id="organizerFacebookId" name="organizerFacebookId" />
            	<input type="hidden" id="timeString" name="timeString"/>
            	<input type="hidden" id="locationString" name="locationString"/>
            	<input type="hidden" id="attendeeFidString" name="attendeeFidString"/>
            	
                <h2 class="form-create-heading">Create an Event</h2>
                <input type="text" class="input-block-level" name="title" id="title" placeholder="Event Name" />
                <input type="text" class="input-block-level" name="detail" id="detail" placeholder="Contents of the event" />
                    
                    <div class="input-append date form_datetime">
                        <input size="16" type="text" value="" readonly>
                            <span class="add-on"><i class="icon-calendar"></i></span>
                            <a id="addBtn" class="btn btn-primary" >Add the Time</a>
                    </div>
                    <div class = "hero-unit">
                        <b>Selected Times:</b>
                        <div id="time" name="Times"></div>
                    </div>
                    
                    <script type="text/javascript">
                        var date = new Date();
						
                        date.setDate(date.getUTCDate());
                        var timepicker = $(".form_datetime");

                        timepicker.datetimepicker({
                                                           format: "yyyy-mm-dd hh:ii",
                                                           autoclose: true,
                                                           todayBtn: false,
                                                           startDate: date,
                                                           minuteStep: 10,
                                                           linkField: "mirror_field",
                                                           linkFormat: "yyyy-mm-dd hh:ii"
                                                });
                        
                        $("#addBtn").click(function(){
                                           
                                           if(timepicker.find("input").val()!="") {
                                        
                                            document.getElementById('time').innerHTML += '<li id=\"time'+timenum+'\">'
                                            +timepicker.find("input").val()+'<a href=\"#\"><span class=\"label label-warning\" onclick=\"deleteTime(' + timenum +')\" style=\"float:right\"> delete</span></a></li>';
                                            
                                            //document.getElementById('time').innerHTML += '<input type=\"hidden\" id=\"time\"'+ timenum+'\"/>
                                            times.push(timepicker.find("input").val());
                                            timenum++;
                                           }});
                        
                
                                                           
                    </script>
                    <div id="google-map-canvas"></div>
                    
                    
                    <div class = "hero-unit">
                        <b>Selected Corresponding Locations:</b>
                        <div id="address" Name = "Locations"></div>
                    </div>
                    
                    <p></p>

                <div id="fb-root"></div>
                
            </form>
            
            <button class="btn btn-large btn-primary" onclick="invite()">Create and Invite Friends!</button>
            
            <hr>
            
            <footer style="text-align:center">
                <p>&copy; Columbia University 2014</p>
            </footer>
        </div>

    </body>
</html>

