package edu.sjsu.cmpe.procurement.config;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.client.JerseyClientConfiguration;
import com.yammer.dropwizard.config.Configuration;

public class ProcurementServiceConfiguration extends Configuration {
	 @NotEmpty
	    @JsonProperty
	    private String stompQueueName;

	    @NotEmpty
	    @JsonProperty
	    private String stompCompTopicName;
	    
	    @NotEmpty
	    @JsonProperty
	    private String stompSelfCompTopicName;
	    
	    @NotEmpty
	    @JsonProperty
	    private String stompComicCompTopicName;
	    
	    @NotEmpty
	    @JsonProperty
	    private String stompMgmtCompTopicName;

	    @NotEmpty
	    @JsonProperty
	    private String apolloUser;
	    
	    @NotEmpty
	    @JsonProperty
	    private String apolloPassword;
	    
	    @NotEmpty
	    @JsonProperty
	    private String apolloHost;
	    
	    @NotEmpty
	    @JsonProperty
	    private String apolloPort;

    @Valid
    @NotNull
    @JsonProperty
    private JerseyClientConfiguration httpClient = new JerseyClientConfiguration();

    /**
     * 
     * @return
     */
    public JerseyClientConfiguration getJerseyClientConfiguration() {
	return httpClient;
    }

    /**
     * @return the stompQueueName
     */
    public String getStompQueueName() {
	return stompQueueName;
    }

    /**
     * @param stompQueueName
     *            the stompQueueName to set
     */
    public void setStompQueueName(String stompQueueName) {
	this.stompQueueName = stompQueueName;
    }

    public String getstompCompTopicName() {
    	return stompCompTopicName;
        }

        
        public void setstompCompTopicName(String stompCompTopicName) {
    	this.stompCompTopicName = stompCompTopicName;
        }
        public String getstompSelfCompTopicName() {
        	return stompSelfCompTopicName;
            }

            
            public void setstompSelfCompTopicName(String stompSelfCompTopicName) {
        	this.stompSelfCompTopicName = stompSelfCompTopicName;
            }
            public String getstompComicCompTopicName() {
            	return stompComicCompTopicName;
                }

                
                public void setstompComicCompTopicName(String stompComicCompTopicName) {
            	this.stompComicCompTopicName = stompComicCompTopicName;
                }
                public String getstompMgmtCompTopicName() {
                	return stompMgmtCompTopicName;
                    }

                    
                    public void setstompMgmtCompTopicName(String stompMgmtCompTopicName) {
                	this.stompMgmtCompTopicName = stompMgmtCompTopicName;
                    }
        public String getApolloUser() {
        	return apolloUser;
            }
        public void setApolloUser(String apolloUser) {
        	this.apolloUser = apolloUser;
            }
        public String getApolloPassword() {
        	return apolloPassword;
            }
        public void setApolloPassword(String apolloPassword) {
        	this.apolloPassword = apolloPassword;
            }
        public String getApolloHost() {
        	return apolloHost;
            }
        public void setApolloHost(String apolloHost) {
        	this.apolloHost = apolloHost;
            }
        public String getApolloPort() {
        	return apolloPort;
            }
        public void setApolloPort(String apolloPort) {
        	this.apolloPort = apolloPort;
            }
}
