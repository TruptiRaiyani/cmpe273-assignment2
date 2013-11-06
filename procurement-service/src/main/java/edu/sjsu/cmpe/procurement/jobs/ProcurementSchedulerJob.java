package edu.sjsu.cmpe.procurement.jobs;

import java.util.ArrayList;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.core.MediaType;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import de.spinscale.dropwizard.jobs.Job;
import de.spinscale.dropwizard.jobs.annotations.Every;
import edu.sjsu.cmpe.procurement.ProcurementService;

/**
 * This job will run at every 5 second.
 */
@Every("5min")
public class ProcurementSchedulerJob extends Job {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void doJob() {
	String strResponse = ProcurementService.jerseyClient.resource(
		"http://ip.jsontest.com/").get(String.class);
	log.debug("Response from jsontest.com: {}", strResponse);
	try {
	String queueName = "/queue/72107.book.orders";
	String ComptopicName = "/topic/72107.book.computer";
	String AlltopicName = "/topic/72107.book.all";
	//String SelftopicName = "/topic/72107.book.selfimprovement";
	//String MgmttopicName = "/topic/72107.book.management";
	String apolloUser = "admin";
	String apolloPassword = "password";
	String apolloHost = "54.215.210.214";
	int apolloPort = 61613;
	log.debug("Queue name is {}. Topic is {}", queueName, ComptopicName);
	// TODO: Apollo STOMP Broker URL and login
	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
	factory.setBrokerURI("tcp://" + apolloHost + ":" + apolloPort);

	Connection connection;
	
		connection = factory.createConnection(apolloUser, apolloPassword);
	
	connection.start();
	Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	
	
	// Start procurement service to get data from queue ... book lost
	Destination dest = new StompJmsDestination(queueName);
	MessageConsumer consumer = session.createConsumer(dest);
	System.out.println("Waiting for messages from " + queueName + "...");
	long waitUntil = 5000;
	ArrayList<Integer> bookLostID = new ArrayList<Integer>();
	while(true) {
	    Message msg = consumer.receive(waitUntil);
	    if( msg instanceof  TextMessage ) {
		String body = ((TextMessage) msg).getText();
		System.out.println("Received message = " + body);
		//System.out.println("Received message = " + body.substring(body.indexOf(":") + 2,body.indexOf("}")));
		
		bookLostID.add(Integer.parseInt(body.substring(body.indexOf(":") + 2,body.indexOf("}"))));
		//System.out.println(bookLostID);
	    } else if (msg == null) {
	          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
	          break;
	    } else {
	         System.out.println("Unexpected message type: " + msg.getClass());
	    }
	}
	
	// End procurement service to get data from queue ... book lost
	
	
	
	// Start code for http post for book lost to publisher
	
	Client client = Client.create();
	 
	WebResource webResource1 = client
	   .resource("http://54.215.210.214:9000/orders");

	
	JSONObject orders = new JSONObject();
	
	orders.put("id", "72107");
	orders.put("order_book_isbns", bookLostID);
	//System.out.println("order json " + orders);
	
	ClientResponse response1 = webResource1.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, orders.toString() );
	
	System.out.println("Output from Server .... \n");
	String output1 = response1.getEntity(String.class);
	System.out.println(output1);
// End code for http post for book lost to publisher
	
	
	
	// Start publisher code to send new books to topic
	
	Destination destComptopicName = new StompJmsDestination(ComptopicName);
	Destination destALLtopicName = new StompJmsDestination(AlltopicName);
	//Destination destSelftopicName = new StompJmsDestination(SelftopicName);
	//Destination destMgmttopicName = new StompJmsDestination(MgmttopicName);
	

	 
	WebResource webResource = client
	   .resource("http://54.215.210.214:9000/orders/72107");

	ClientResponse response = webResource.accept("application/json")
               .get(ClientResponse.class);

	String output = response.getEntity(String.class);
	JSONObject jsonObj = new JSONObject(output);
	//System.out.println("test 1  " +output);
	//System.out.println("test 2  " + jsonObj);
	JSONObject testjson = null;
	JSONArray j = jsonObj.getJSONArray("shipped_books");
	String isbn, title,category,coverimage = "";
	 MessageProducer producerComputer = null;
      for(int i = 0; i < j.length(); i++){
    	  testjson = j.getJSONObject(i);
    	  isbn=testjson.getString("isbn");
          title=testjson.getString("title");
          category=testjson.getString("category");
          coverimage=testjson.getString("coverimage");
      
         // System.out.println(isbn + " : \"" +title +  "\" : \"" +category + "\" : \"" +coverimage + "\"");
         String data = isbn + ", \"" +title +  "\", \"" +category + "\", \"" +coverimage + "\"";
          
          //  String data = "Hello World sdsdsfsd";
          TextMessage msg = session.createTextMessage(data.toString());
          msg.setLongProperty("id", System.currentTimeMillis());
         
          if(category.equals("computer"))
          {
        	   producerComputer = session.createProducer(destComptopicName);
        		producerComputer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        		producerComputer.send(msg);
        		//System.out.println("comp");
          }
          MessageProducer m = session.createProducer(destALLtopicName);
        //  m = 
    	  m.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
    	  m.send(msg);
    	 // System.out.println("comics");
         /* else if(category.equals("comics"))
          {
        	  producerComputer = session.createProducer(destComictopicName);
        	  producerComputer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        	  producerComputer.send(msg);
        	  System.out.println("comics");
          }
          else if(category.equals("management"))
          {
        	  producerComputer = session.createProducer(destMgmttopicName);
        	  producerComputer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        	  producerComputer.send(msg);
        	  System.out.println("mgmt");
          }
          else if(category.equals("selfimprovement"))
          {
        	  producerComputer = session.createProducer(destSelftopicName);
        	  producerComputer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        	  producerComputer.send(msg);
        	  System.out.println("sa");
          }*/
      	
      	
      	//System.out.println(msg);
      }
      
 
     
   // End publisher code to send new books to topic
	connection.close();
	} catch (JMSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
}
