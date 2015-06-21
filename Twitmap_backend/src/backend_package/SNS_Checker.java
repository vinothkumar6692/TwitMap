package backend_package;

import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



public class SNS_Checker implements Runnable {
private AmazonSQS sqs;
private ReceiveMessageRequest receiveMessageRequest;
private CreateTopicRequest createReq;
private CreateTopicResult createRes;

	public SNS_Checker(AmazonSQS sqs,ReceiveMessageRequest receiveMessageRequest,CreateTopicRequest createReq,CreateTopicResult createRes)//AmazonSNSClient snsservice
{
	this.sqs = sqs;
	this.receiveMessageRequest = receiveMessageRequest;
	this.createReq = createReq;
	this.createRes = createRes;
	
}
	@Override
	public void run() {
		String Latitude = null;
		String Longtitude = null;
		String tweetText = null;
		String Text = null;
		String Sentiment = null;
		AWSCredentials credentials =new BasicAWSCredentials("","");
    	AmazonSNSClient snsservice = new AmazonSNSClient(credentials); 
		CreateTopicRequest createReq = new CreateTopicRequest().withName("sentsns");
		CreateTopicResult createRes = snsservice.createTopic(createReq);
        List<Message> recd_message = sqs.receiveMessage(receiveMessageRequest.withMessageAttributeNames("All")).getMessages();
        for (Message message : recd_message) {
            
            for (Entry<String, MessageAttributeValue> entry : message.getMessageAttributes().entrySet()) {
                
                
                if(entry.getKey().toString().equalsIgnoreCase("latitude"))
        		{
                	Latitude = entry.getValue().getStringValue();
        		}
                if(entry.getKey().toString().equalsIgnoreCase("longtitude"))
        		{
                	Longtitude = entry.getValue().getStringValue();
        		}
                if(entry.getKey().toString().equalsIgnoreCase("Text"))
                {
                	tweetText = entry.getValue().getStringValue();
                }
                
            }
            tweetText=tweetText.replaceAll("\\s+", "%20");
    	    try{
    		URL url = new URL("http://access.alchemyapi.com/calls/text/TextGetTextSentiment?apikey=bde9fa4d82dadee5e144b70a52f67d0929c9037d&text="+tweetText);
    		URLConnection Connection = url.openConnection();
    		
    		DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
    		DocumentBuilder builder1 = factory1.newDocumentBuilder();
    		Document doc1 = builder1.parse(Connection.getInputStream());
    		
    		TransformerFactory factory2 = TransformerFactory.newInstance();
    		Transformer xform1 = factory2.newTransformer();
    		xform1.transform(new DOMSource(doc1), new StreamResult(System.out));
    		NodeList nodeList = doc1.getElementsByTagName("docSentiment");
    		
    		for (int temp = 0; temp < nodeList.getLength(); temp++) {
    			Node Node1 = nodeList.item(temp);
    			if (Node1.getNodeType() == Node.ELEMENT_NODE) {
    				Element Element1 = (Element) Node1;
    			Sentiment = Element1.getElementsByTagName("type").item(0).getTextContent();
    			}
    		}
    		
    	    }
    	    catch(Exception e)
    	    {
    	    	
    	    }
    	    PublishRequest publishReq = new PublishRequest().withTopicArn(createRes.getTopicArn()).withMessage(Latitude +","+Longtitude+","+Sentiment+","+tweetText);
            snsservice.publish(publishReq);		
            String messageRecieptHandle = message.getReceiptHandle();
           
        
        }
		
	}
	
            
		
	}


