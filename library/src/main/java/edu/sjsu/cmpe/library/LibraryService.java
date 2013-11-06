package edu.sjsu.cmpe.library;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

import edu.sjsu.cmpe.library.api.resources.BookResource;
import edu.sjsu.cmpe.library.api.resources.RootResource;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.Book.Status;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.ui.resources.HomeResource;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

public class LibraryService extends Service<LibraryServiceConfiguration> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private  BookRepositoryInterface bookRepository1 ;
    private  LibraryServiceConfiguration configuration1 ;
    public static void main(String[] args) throws Exception {
	new LibraryService().run(args);
    }

    @Override
    public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap)  {
    	
    	//Thread thread1 = new Thread () {
    		//  public void run () {
    			  bootstrap.setName("library-service");
    			  bootstrap.addBundle(new ViewBundle());
    			 
    			 
    		 // }
    		//};
    		//thread1.start();
    				//final BookRepository b = new BookRepository();
    				final String user = "admin";
    				final String password ="password";
          			final String host ="54.215.210.214";
          			final int port = Integer.parseInt("61613");
          			final String ComptopicName =  "/topic/72107.book.computer";
          			final String ALLtopicName =  "/topic/72107.book.all";
          		/*	final String ComicstopicName = "/topic/72107.book.comics";
          			final String SelftopicName =  "/topic/72107.book.selfimprovement";
          			final String MgmttopicName =  "/topic/72107.book.management";
          			final long waitUntil = 5000;*/
          			
          			
          			
    		Thread thread2 = new Thread () {
      		  public void run () {
      			
      			try
      			{
      				//configuration.getApolloHost();
      				
      				StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
          			factory.setBrokerURI("tcp://" + host + ":" + port);
      				Connection connection;          			
      				connection = factory.createConnection(user, password);      			
      			connection.start();
      			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      			Destination destComptopicName = null;
      			//if(configuration1.getInstanceName().equals("library-b"))
      			 //destComptopicName = new StompJmsDestination(ComptopicName);
      			//else if(configuration1.getInstanceName().equals("library-a"))
      			 destComptopicName = new StompJmsDestination(ComptopicName);
      			MessageConsumer consumerComptopic = session.createConsumer(destComptopicName);
      			System.currentTimeMillis();
      			System.out.println("Waiting for messages...");
      			while(true) {
      			    Message msg = consumerComptopic.receive();
      			    if( msg instanceof  TextMessage ) {
      				String body = ((TextMessage) msg).getText();
      				
      				System.out.println("Received message = " + body);
      				String[] data = body.split(",");
      				Book newbook = new Book();
      				//newbook.setIsbn(3);
      				newbook.setIsbn((long) Integer.parseInt(data[0]));
      				newbook.setTitle(data[1].replace("\"", ""));
      				newbook.setCategory(data[2].replace("\"", ""));
      				
      				try {
      					newbook.setCoverimage(new URL(data[3].replace("\"", "")));
      				} catch (MalformedURLException e) {
      				    // eat the exception
      				}
      				
      				if(configuration1.getInstanceName().equals("library-b"))
      				{
      				if(bookRepository1.getBookByISBN((long) Integer.parseInt(data[0])) == null)
      					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
      				else
      				{
      					Book b = bookRepository1.getBookByISBN((long) Integer.parseInt(data[0]));
      					b.setStatus(Status.available);
      					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
      				}
      				} 				
      			    }else if (msg == null) {
      			       //   System.out.println("No new messages. Existing due to timeout - " +  " sec");
      			          break;
      			    }else {
      				System.out.println("Unexpected message type: "+msg.getClass());
      			    }
      			}
      				
      		 } 
      			catch (JMSException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
      			
      			
      		 }
    		};
    		thread2.start();
    		Thread thread6 = new Thread () {
        		  public void run () {
        			
        			try
        			{
        				//configuration.getApolloHost();
        				
        				StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
            			factory.setBrokerURI("tcp://" + host + ":" + port);
        				Connection connection;          			
        				connection = factory.createConnection(user, password);      			
        			connection.start();
        			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        			Destination destComptopicName = null;
        			//if(configuration1.getInstanceName().equals("library-b"))
        			 //destComptopicName = new StompJmsDestination(ComptopicName);
        			//else if(configuration1.getInstanceName().equals("library-a"))
        			 destComptopicName = new StompJmsDestination(ALLtopicName);
        			MessageConsumer consumerComptopic = session.createConsumer(destComptopicName);
        			System.currentTimeMillis();
        			System.out.println("Waiting for messages...");
        			while(true) {
        			    Message msg = consumerComptopic.receive();
        			    if( msg instanceof  TextMessage ) {
        				String body = ((TextMessage) msg).getText();
        				
        				System.out.println("Received message = " + body);
        				String[] data = body.split(",");
        				Book newbook = new Book();
        				//newbook.setIsbn(3);
        				newbook.setIsbn((long) Integer.parseInt(data[0]));
        				newbook.setTitle(data[1].replace("\"", ""));
        				newbook.setCategory(data[2].replace("\"", ""));
        				
        				try {
        					newbook.setCoverimage(new URL(data[3].replace("\"", "")));
        				} catch (MalformedURLException e) {
        				    // eat the exception
        				}
        				
        				if(configuration1.getInstanceName().equals("library-a"))
        				{
        				if(bookRepository1.getBookByISBN((long) Integer.parseInt(data[0])) == null)
        					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
        				else
        				{
        					Book b = bookRepository1.getBookByISBN((long) Integer.parseInt(data[0]));
        					b.setStatus(Status.available);
        					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
        				}
        				} 				
        			    }else if (msg == null) {
        			        //  System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
        			          break;
        			    }else {
        				System.out.println("Unexpected message type: "+msg.getClass());
        			    }
        			}
        				
        		 } 
        			catch (JMSException e) {
      				// TODO Auto-generated catch block
      				e.printStackTrace();
      			}
        			
        			
        		 }
      		};
      		thread6.start();
      		/*Thread thread5 = new Thread () {
        		  public void run () {
        			
        			try
        			{
        				//configuration.getApolloHost();
        				
        				StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
            			factory.setBrokerURI("tcp://" + host + ":" + port);
        				Connection connection;          			
        				connection = factory.createConnection(user, password);      			
        			connection.start();
        			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        			Destination destComptopicName = null;
        			//if(configuration1.getInstanceName().equals("library-b"))
        			 //destComptopicName = new StompJmsDestination(ComptopicName);
        			//else if(configuration1.getInstanceName().equals("library-a"))
        			 destComptopicName = new StompJmsDestination(MgmttopicName);
        			MessageConsumer consumerComptopic = session.createConsumer(destComptopicName);
        			System.currentTimeMillis();
        			System.out.println("Waiting for messages...");
        			while(true) {
        			    Message msg = consumerComptopic.receive();
        			    if( msg instanceof  TextMessage ) {
        				String body = ((TextMessage) msg).getText();
        				
        				System.out.println("Received message = " + body);
        				String[] data = body.split(",");
        				Book newbook = new Book();
        				//newbook.setIsbn(3);
        				newbook.setIsbn((long) Integer.parseInt(data[0]));
        				newbook.setTitle(data[1].replace("\"", ""));
        				newbook.setCategory(data[2].replace("\"", ""));
        				
        				try {
        					newbook.setCoverimage(new URL(data[3].replace("\"", "")));
        				} catch (MalformedURLException e) {
        				    // eat the exception
        				}
        				
        				if(configuration1.getInstanceName().equals("library-a"))
        				{
        				if(bookRepository1.getBookByISBN((long) Integer.parseInt(data[0])) == null)
        					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
        				else
        				{
        					Book b = bookRepository1.getBookByISBN((long) Integer.parseInt(data[0]));
        					b.setStatus(Status.available);
        					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
        				}
        				} 				
        			    }else if (msg == null) {
        			          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
        			          break;
        			    }else {
        				System.out.println("Unexpected message type: "+msg.getClass());
        			    }
        			}
        				
        		 } 
        			catch (JMSException e) {
      				// TODO Auto-generated catch block
      				e.printStackTrace();
      			}
        			
        			
        		 }
      		};
      		thread5.start();
    		
    		Thread thread3 = new Thread () {
        		  public void run () {
        			  try
            			{
            				//configuration.getApolloHost();
            				
            				StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
                			factory.setBrokerURI("tcp://" + host + ":" + port);
            				Connection connection;          			
            				connection = factory.createConnection(user, password);      			
            			connection.start();
            			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            			Destination destComptopicName = null;
            			//if(configuration1.getInstanceName().equals("library-b"))
            			 //destComptopicName = new StompJmsDestination(ComptopicName);
            			//else if(configuration1.getInstanceName().equals("library-a"))
            			 destComptopicName = new StompJmsDestination(ComptopicName);
            			MessageConsumer consumerComptopic = session.createConsumer(destComptopicName);
            			System.currentTimeMillis();
            			System.out.println("Waiting for messages...");
            			while(true) {
            			    Message msg = consumerComptopic.receive();
            			    if( msg instanceof  TextMessage ) {
            				String body = ((TextMessage) msg).getText();
            				
            				System.out.println("Received message = " + body);
            				String[] data = body.split(",");
            				Book newbook = new Book();
            				//newbook.setIsbn(3);
            				newbook.setIsbn((long) Integer.parseInt(data[0]));
            				newbook.setTitle(data[1].replace("\"", ""));
            				newbook.setCategory(data[2].replace("\"", ""));
            				
            				try {
            					newbook.setCoverimage(new URL(data[3].replace("\"", "")));
            				} catch (MalformedURLException e) {
            				    // eat the exception
            				}
            				//if(configuration1.getInstanceName().equals("library-b"))
            				//{
            				//	System.out.println((long) Integer.parseInt(data[0]));
            				if(bookRepository1.getBookByISBN((long) Integer.parseInt(data[0])) == null)
            					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
            				else
            				{
            					Book b = bookRepository1.getBookByISBN((long) Integer.parseInt(data[0]));
            					b.setStatus(Status.available);
            					bookRepository1.UpdateBook(newbook,newbook.getIsbn());
            				}
            				//}   				
            			    }else if (msg == null) {
            			          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
            			          break;
            			    }else {
            				System.out.println("Unexpected message type: "+msg.getClass());
            			    }
            			}
            				}
            		 
            			catch (JMSException e) {
          				// TODO Auto-generated catch block
          				e.printStackTrace();
          			}
        		  }
    		};
    		thread3.start();
    		/*Thread thread3 = new Thread () {
        		  public void run () {
        			
        			try
        			{
        				StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
              			factory.setBrokerURI("tcp://" + host + ":" + port);
          				Connection connection;          			
          				connection = factory.createConnection(user, password);      			
          			connection.start();
          			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
          			
          			Destination destComictopicName = new StompJmsDestination(ComictopicName);
        				MessageConsumer consumerComictopiComic = session.createConsumer(destComictopicName);
        				System.currentTimeMillis();
        				System.out.println("Waiting for messages...");
        				while(true) {
        				    Message msg = consumerComictopiComic.receive();
        				    if( msg instanceof  TextMessage ) {
        					String body = ((TextMessage) msg).getText();
        					
        					System.out.println("Received message = " + body);

        				    }else if (msg == null) {
        				          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
        				          break;
        				    }else {
        					System.out.println("Unexpected message type: "+msg.getClass());
        				    }
        				}
        			} 
          			catch (JMSException e) {
        				// TODO Auto-generated catch block
        				e.printStackTrace();
        			}
          		 }
        		};
        		thread3.start();
        		Thread thread4 = new Thread () {
          		  public void run () {
          			
          			try
          			{
          				StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
              			factory.setBrokerURI("tcp://" + host + ":" + port);
          				Connection connection;          			
          				connection = factory.createConnection(user, password);      			
          			connection.start();
          			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
          			
          			Destination destMgmttopicName = new StompJmsDestination(MgmttopicName);
          				MessageConsumer consumerMgmt = session.createConsumer(destMgmttopicName);
          				System.currentTimeMillis();
          				System.out.println("Waiting for messages...");
          				while(true) {
          				    Message msg = consumerMgmt.receive();
          				    if( msg instanceof  TextMessage ) {
          					String body = ((TextMessage) msg).getText();
          					
          					System.out.println("Received message = " + body);

          				    }else if (msg == null) {
          				          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
          				          break;
          				    }else {
          					System.out.println("Unexpected message type: "+msg.getClass());
          				    }
          				}
          			} 
            			catch (JMSException e) {
          				// TODO Auto-generated catch block
          				e.printStackTrace();
          			}
            		 }
          		};
          		thread4.start();
          		Thread thread5 = new Thread () {
          		  public void run () {
          			
          			try
          			{
          				StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
              			factory.setBrokerURI("tcp://" + host + ":" + port);
          				Connection connection;          			
          				connection = factory.createConnection(user, password);      			
          			connection.start();
          			 Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
          			
          			Destination destSelftopicName = new StompJmsDestination(SelftopicName);
          				MessageConsumer consumerSeft = session.createConsumer(destSelftopicName);
          				System.currentTimeMillis();
          				System.out.println("Waiting for messages...");
          				while(true) {
          				    Message msg = consumerSeft.receive();
          				    if( msg instanceof  TextMessage ) {
          					String body = ((TextMessage) msg).getText();
          					
          					System.out.println("Received message = " + body);

          				    }else if (msg == null) {
          				          System.out.println("No new messages. Existing due to timeout - " + waitUntil / 1000 + " sec");
          				          break;
          				    }else {
          					System.out.println("Unexpected message type: "+msg.getClass());
          				    }
          				}
          			          		
          			} 
            			catch (JMSException e) {
          				// TODO Auto-generated catch block
          				e.printStackTrace();
          			}
            		 }
          		};
          		thread5.start();
          			*/
          			
          			
    
        			
		
	
		
    }

    @Override
    public void run(final LibraryServiceConfiguration configuration,
	    final Environment environment) throws Exception {
	// This is how you pull the configurations from library_x_config.yml
    	Thread thread4 = new Thread () {
  		  public void run () {
  			  try
      			{
    	configuration1 = configuration;
    	BookRepositoryInterface bookRepository = new BookRepository();
    	bookRepository1 = bookRepository;
    	String queueName = configuration.getStompQueueName();
    	String topicName = configuration.getStompTopicName();
     	String apolloUser = configuration.getApolloUser();
    	String apolloPassword = configuration.getApolloPassword();
    	String apolloHost = configuration.getApolloHost();
    	String instanceName = configuration.getInstanceName();
    	int apolloPort = Integer.parseInt(configuration.getApolloPort());
    	log.debug("Queue name is {}. Topic name is {}", queueName,
    		topicName);
    	// TODO: Apollo STOMP Broker URL and login

    	/** Root API */
//    	String destination = arg(args, 0, queue);

    	StompJmsConnectionFactory factory = new StompJmsConnectionFactory();
    	factory.setBrokerURI("tcp://" + apolloHost + ":" + apolloPort);

    	Connection connection = factory.createConnection( apolloUser , apolloPassword);    
    	connection.start();
	
	
	environment.addResource(RootResource.class);
	/** Books APIs */
	
	environment.addResource(new BookResource(bookRepository,connection ,queueName, topicName,instanceName));

	/** UI Resources */
	environment.addResource(new HomeResource(bookRepository));
	
      			 
      			}
     		 
  			catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
	};
	thread4.start();
    }
   
}
