package backend_package;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;


public class Worker_Pool {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AWSCredentials credentials =new BasicAWSCredentials("","");
		AmazonSQS SQS_obj= new AmazonSQSClient(credentials);
		String SQS_url="";
		AmazonSNSClient SNS_obj = new AmazonSNSClient(credentials); //create SNS service
		CreateTopicRequest createReq = new CreateTopicRequest().withName("sentsns");
		CreateTopicResult createRes = SNS_obj.createTopic(createReq);
		for (String Queue_Url : SQS_obj.listQueues().getQueueUrls()) {
			if(Queue_Url.contains("SQS_Queue_1"))
		    {
				SQS_url = Queue_Url; 
		    }
		    }
		ReceiveMessageRequest receiveMessageRequest = 
			    new ReceiveMessageRequest();             
			receiveMessageRequest.setQueueUrl(SQS_url);
try{
		ExecutorService executor = Executors.newFixedThreadPool(10) ;
		while(true)
		{
			Runnable worker = new SNS_Checker(SQS_obj,receiveMessageRequest,createReq,createRes);
			executor.execute(worker);
		}
}
catch(Exception e)
{
	
}
	}

}

