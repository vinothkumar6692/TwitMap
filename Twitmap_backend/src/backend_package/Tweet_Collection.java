package backend_package;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClient;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Tweet_Collection {
			/* Specify the AWS Credentials in the "credentials" object*/
			
		    final static int max = 100000;
			static int count = 0;
			public static void main(String[] args) throws TwitterException {
		    AWSCredentials credentials =new BasicAWSCredentials("","");
		    final AmazonSQS sqs= new AmazonSQSClient(credentials);
			Region usEast1 = Region.getRegion(Regions.US_EAST_1);
			sqs.setRegion(usEast1);
		    CreateQueueRequest createQueueRequest = new CreateQueueRequest("SQS_Queue_1");
    		final String QueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
    
    		/* Specify the Twitter account credentials*/
    	 ConfigurationBuilder cb = new ConfigurationBuilder();
    	 cb.setDebugEnabled(true)
         .setOAuthConsumerKey("")
         .setOAuthConsumerSecret("")
         .setOAuthAccessToken("")
         .setOAuthAccessTokenSecret("");
        try
        {
        final TwitterStream tweet_Collector = new TwitterStreamFactory(cb.build()).getInstance();    
    	final Connection conn = Tweet_DB.createConnection();
        StatusListener listener = new StatusListener() {
            @Override
            public void onStatus(Status status) {
            	if(count<max)
            	{ 
					if(status.getId()!=0 && status.getGeoLocation()!=null&&status.getLang().equalsIgnoreCase("en"))
					{
	            		PreparedStatement preparedStatement = null;
	            		try {
							preparedStatement = conn.prepareStatement("insert into twitter values(?,?,?,?,?)");
							preparedStatement.setString(1, Long.toString(status.getId()));
							preparedStatement.setString(2,status.getUser().getScreenName() );
							preparedStatement.setString(3, status.getText());
							preparedStatement.setDouble(4, status.getGeoLocation().getLatitude());
							preparedStatement.setDouble(5, status.getGeoLocation().getLongitude());
							preparedStatement.executeUpdate();
							Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
							messageAttributes.put("latitude", new MessageAttributeValue().withDataType("Number.latitude").withStringValue(String.valueOf(status.getGeoLocation().getLatitude())));
							messageAttributes.put("longtitude", new MessageAttributeValue().withDataType("Number.longtitude").withStringValue(String.valueOf(status.getGeoLocation().getLongitude())));
							messageAttributes.put("Text", new MessageAttributeValue().withDataType("String.Name").withStringValue(status.getText()));	
							SendMessageRequest new_request = new SendMessageRequest();
							new_request.withMessageBody(status.getUser().getScreenName());
							new_request.withQueueUrl(QueueUrl);
							new_request.withMessageAttributes(messageAttributes);
							sqs.sendMessage(new_request);							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            		count++;
					}
            	}
					else{
	            		tweet_Collector.removeListener(this);
	    				tweet_Collector.shutdown();	
						
	            	}          
            }

            @Override
            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
             //   System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
            }

            @Override
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
               // System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
            }

            @Override
            public void onScrubGeo(long userId, long upToStatusId) {
                //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
            }

            @Override
            public void onStallWarning(StallWarning warning) {
                //System.out.println("Got stall warning:" + warning);
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };
        tweet_Collector.addListener(listener);
        tweet_Collector.sample();
     }
     catch(Exception e)
     {
    	System.out.println(e); 
     }
        
    }
}