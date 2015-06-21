
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import org.json.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class SNS_receiver
 */
public class SNS_receiver extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Enumeration<String> reqAttribNames;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SNS_receiver() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try{
			AWSCredentials credentials =new BasicAWSCredentials("AKIAIDZKESMRN4NYNO3Q","W5EqQLabPl0wVvYlOXP7VGSGpJHBc1cpjqy03lb4");	
			String subscribeURL = "arn:aws:sns:us-east-1:326984831102:sentsns";
			Scanner scanner = new Scanner(request.getInputStream());
	        StringBuilder builder1 = new StringBuilder();
	        String status_code = "";
	        String msg;
	        while (scanner.hasNextLine()) {
	            builder1.append(scanner.nextLine());
	        }
	        InputStream stream = new ByteArrayInputStream(builder1.toString().getBytes());
	        Map<String, String> message = new ObjectMapper().readValue(stream, Map.class);
			reqAttribNames = request.getAttributeNames();
			status_code = response.getContentType();			 
			String messagetype = request.getHeader("x-amz-sns-message-type");			 
				if (messagetype.equals("Notification")) {		
					 Scanner scan = new Scanner(request.getInputStream());
					    StringBuilder builder2 = new StringBuilder();
					    while (scan.hasNextLine()) {
					      builder2.append(scan.nextLine());
					    }
					    InputStream bytes = new ByteArrayInputStream(builder1.toString().getBytes());
					    JSONParser jsonParser = new JSONParser(); 
				        JSONObject jsonObject = null;
						try {
							jsonObject = (JSONObject) jsonParser.parse(new String(builder1));
						} 
						catch (ParseException e) 
						{
							e.printStackTrace();
						}
						//Store the received message and parse it to 
						//get the Latitude, Longitude and Sentiment 
						//and Insert it into the table
						msg = (String)jsonObject.get("Message");
						String receivedContent[] = msg.split(",");
					    String Latitude = receivedContent[0];
					    String Longtitude = receivedContent[1];
					    String Sentiment = receivedContent[2];
						Connection conn=Connection_class.createConnection();
						PreparedStatement ps = null;
						ps=conn.prepareStatement("insert into sentiment values (?,?,?)");
				        ps.setString(1, Latitude);
				        ps.setString(2, Longtitude);
				        ps.setString(3, Sentiment);
				        ps.executeUpdate();
				        try 
				        {				        	
				        }
				        catch (Exception e)
				        {
				        	e.printStackTrace();
				        }					
				}
				  if (messagetype.equalsIgnoreCase("SubscriptionConfirmation"))
					{
				    Scanner scan = new Scanner(request.getInputStream());
				    StringBuilder builder3 = new StringBuilder();
				    while (scan.hasNextLine()) {
				      builder3.append(scan.nextLine());
				    }
				    InputStream bytes = new ByteArrayInputStream(builder1.toString().getBytes());
			        Map<String, String> message1 = new ObjectMapper().readValue(bytes, Map.class);
				    JSONParser jsonParser = new JSONParser(); 
			        JSONObject jsonObject = null;
			        try {
						jsonObject = (JSONObject) jsonParser.parse(new String(builder1));
					} 
			        catch (ParseException e) 
			        {
						e.printStackTrace();
					}
					AmazonSNSClient sns = new AmazonSNSClient(credentials);      
	                sns.confirmSubscription(new ConfirmSubscriptionRequest(message.get("TopicArn"), message.get("Token")));
			        String subscribe_url=(String)jsonObject.get("SubscribeURL");
			        System.out.println("Subscribe URL: "+ subscribe_url );
			        response.setContentType("text/html");
			        response.setStatus(HttpServletResponse.SC_OK);
			    	Scanner scan1 = new Scanner(new URL(subscribe_url).openStream());
			    	StringBuilder sb = new StringBuilder();
			    	while (scan1.hasNextLine()) {
			        sb.append(scan1.nextLine());		 
			       }	
			}
				  response.sendRedirect(request.getContextPath()+"/index.jsp");
		}			
		catch(Exception e)
		{
			System.out.println(e);
		}
	}

}
