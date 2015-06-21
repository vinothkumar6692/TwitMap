<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Twitter Sentiment Analysis</title>
    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp&libraries=visualization"></script>
   <script type="text/javascript" src="https://www.google.com/jsapi"></script> 
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/css/bootstrap.min.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.4/js/bootstrap.min.js"></script>
   <style>
      html, body{
        height: 100%;
        margin: 0px;
        padding: 0px; 
        background-color: #778899;  
      }
      #map-canvas {
        width: 100%;
        height: 600px;
        }
      #my_chart{
      	margin-left: 110px;
      }
      #text1{
      margin-left:110px}
    </style>
    <script type="text/javascript">
    setTimeout(function(){
    	  window.location.reload(1);
    	}, 5000);

    <%
	String dbName = "twitter";
    String userName = "raghav";
    String password = "raghavabc";
    String hostname = "twitterheatmap.cjkuojdbhc2z.us-east-1.rds.amazonaws.com";
    String port = "3306";
    String jdbcUrl = "jdbc:mysql://" + hostname + ":" +
      port + "/" + dbName + "?user=" + userName + "&password=" + password;

Class.forName("com.mysql.jdbc.Driver");
	Connection conn=DriverManager.getConnection(jdbcUrl);
	Statement stmt=conn.createStatement();
	String query="select count(*) from twitter1 where longitude='positive';";
	ResultSet rs=stmt.executeQuery(query);
	rs.next();
    int positive = rs.getInt(1);
    String query1="select count(*) from twitter1 where longitude='negative';";
	ResultSet rs1=stmt.executeQuery(query1);
	rs1.next();
    int negative = rs1.getInt(1);
    rs1.close();
    String query2="select count(*) from twitter1 where longitude='neutral' OR longitude='null';";
	ResultSet rs2=stmt.executeQuery(query2);
	rs2.next();
    int neutral = rs2.getInt(1);
    rs2.close();
	String query3="select * from twitter1 where longitude='positive';";
	
%>
    function drawChart() {
 	   var positive = <%=positive%>;
 	   var negative = <%=negative%>;
 	   var neutral = <%=neutral%>;
 	    // Create and populate the data table.
 	    var data = google.visualization.arrayToDataTable([
 	      ['Sentiment', 'Count',{ role: 'style' }],
 	      ['Positive', positive, 'green'],
 	      ['Negative', negative, 'red'],
 	      ['Neutral', neutral,'blue'],
 	    ]);
 	    var options = {
 	      title: 'Sentiment Analysis Chart',
 	      width: 900, 
          legend: { position: 'none' },
 	      axes: {
 	            x: {
 	            	0: { side: 'top', label: 'Percentage'} 
 	             }
 	      }
 	    };
 	     // Create and draw the visualization.
 	   var chart = new google.visualization.BarChart(document.getElementById("my_chart"));
       chart.draw(data, options);
       } 
    
    function initialize() { 
    
console.log(<%=positive%>);

var taxiData = [
                <% 
                ResultSet rs3=stmt.executeQuery(query3);
                while(rs3.next()){ %>
            	new google.maps.LatLng(<%=rs3.getDouble("sentiment")%>,<%=rs3.getDouble("latitude")%>),
            	<%}%>
            ];
<% 
rs3.close();
String query4="select * from twitter1 where longitude='negative';";
ResultSet rs4=stmt.executeQuery(query4);
%>
var taxiData1 = [
                <%while(rs4.next()){ %>
            	new google.maps.LatLng(<%=rs4.getDouble("sentiment")%>,<%=rs4.getDouble("latitude")%>),
            	<%}%>
            ];
	  var mapOptions = {
	    zoom: 2,
	    center: new google.maps.LatLng(37.774546, -122.433523),
	    mapTypeId: google.maps.MapTypeId.SATELLITE
	  };

	  map = new google.maps.Map(document.getElementById('map-canvas'),
	      mapOptions);

	  var pointArray = new google.maps.MVCArray(taxiData);
	  var pointArray1 = new google.maps.MVCArray(taxiData1);

	  heatmap1 = new google.maps.visualization.HeatmapLayer({
	    data: pointArray,
	  });
	  
	  heatmap2 = new google.maps.visualization.HeatmapLayer({
		    data: pointArray1,
		  });

	  heatmap1.setMap(map);
	  heatmap2.setMap(map);
	  gradient1 = [
	               'rgba(0, 255, 255, 0)',
	               'rgba(0, 255, 255, 1)',
	               'rgba(0, 225, 255, 1)',
	               'rgba(0, 200, 255, 1)',
	               'rgba(0, 175, 255, 1)',
	               'rgba(0, 160, 255, 1)',
	               'rgba(0, 145, 223, 1)',
	               'rgba(0, 125, 191, 1)',
	               'rgba(0, 110, 255, 1)',
	               'rgba(0, 100, 255, 1)',
	               'rgba(0, 75, 255, 1)',
	               'rgba(0, 50, 255, 1)',
	               'rgba(0, 25, 255, 1)',
	               'rgba(0, 0, 255, 1)'
	             ];
	           // Red - negative
	           gradient2 = [
	               'rgba(255, 255, 0, 0)',
	               'rgba(255, 255, 0, 1)',
	               'rgba(255, 225, 0, 1)',
	               'rgba(255, 200, 0, 1)',
	               'rgba(255, 175, 0, 1)',
	               'rgba(255, 160, 0, 1)',
	               'rgba(255, 145, 0, 1)',
	               'rgba(255, 125, 0, 1)',
	               'rgba(255, 110, 0, 1)',
	               'rgba(255, 100, 0, 1)',
	               'rgba(255, 75, 0, 1)',
	               'rgba(255, 50, 0, 1)',
	               'rgba(255, 25, 0, 1)',
	               'rgba(255, 0, 0, 1)'
	             ];
	          
	  heatmap1.set('gradient', gradient1);  
	  heatmap2.set('gradient', gradient2); 
	}
	function changeRadius() {
		heatmap1.set('radius', heatmap1.get('radius') ? null : 20);
	    heatmap2.set('radius', heatmap2.get('radius') ? null : 20);
	}

	function changeOpacity() {
		heatmap1.set('opacity', heatmap1.get('opacity') ? null : 0.2);
		heatmap2.set('opacity', heatmap2.get('opacity') ? null : 0.2);
	}

	google.maps.event.addDomListener(window, 'load', initialize);
	google.load("visualization", "1", {packages:["corechart"]});
	google.setOnLoadCallback(drawChart);  
</script>
  </head>
  <body>
  <div class="container">
  <div class="jumbotron">
    <h2><b>Twitter Sentiment Analysis</b></h2>
    <p>The following Map displays the results after sentiment analysis on data collected from the Twitter streaming API.<br>
    <font color="blue"> Positive Sentiment: Blue </font><br>
    <font color="red"> Negative Sentiment: Red </font> 
    </p>
  </div>
  <br><center>
    <div class="btn-group">
      <button class="btn btn-primary" onclick="changeRadius()">Change radius</button>
      <button class="btn btn-primary" onclick="changeOpacity()">Change opacity</button>
    </div><br> <br>
   <div id="map-canvas"></div>
   </div><br><br></center>
   <h2 id="text1"><b>Sentiment Trends Chart</b></h2>
   
<div id="my_chart" style="width: 500px; height: 300px"></div>
  </body>
</html>