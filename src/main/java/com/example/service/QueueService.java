package com.example.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueueService {



    // Will need sqs functionality
    @Autowired
//    AmazonSQSClient sqsClient;
    AmazonSQS sqsClient;



    String ALL_ATTRIBUTES = "All";  // VERY case sensitive !!!
    String ATTRIBUTE_NAME = "txID";
    String QUEUE_PREFIX = "queue/";


    /**
     * initial point to instance things
     */
    public void doSQS() {
        System.out.println("doSQS");
    }

    /**
     * Just attempt the queue creation regardless for now, but should be able to check the existing
     * @param queueName
     */
    protected void createQueue( String queueName ) {

        CreateQueueRequest createRequest=new CreateQueueRequest( queueName )
                .addAttributesEntry("MessageRetentionPeriod", "86400");

        try {

            sqsClient.createQueue(createRequest);
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    /**
     * Given message and where to write it, do so
     * @param messageText
     * @param queueName
     */
    public void sendMessage( String messageText, String queueName ) {

        // TODO : improve getting the queue Url
        String queueUrl=QUEUE_PREFIX + queueName;

        // Perhaps check for queue existence (or force creation)
        createQueue( queueName );

        // Generate the request.  Must be a better way than having queue as text in the calls
        SendMessageRequest smr=new SendMessageRequest()
                .withQueueUrl( queueUrl )
                .withMessageBody( messageText )
                .withDelaySeconds( 0 )
                // add attributes
                .addMessageAttributesEntry(ATTRIBUTE_NAME, new MessageAttributeValue()
                        .withDataType("String")
                        .withStringValue("test attrib value for " + messageText)
                );

        // send it to queue
        sqsClient.sendMessage( smr );

    }

    /**
     * For now just grab something from the queue, dump to screen and then remove from queue
     * @param queueName
     */
    public String getMessage( String queueName ){

        // TODO : improve getting the queue Url
        String queueUrl = QUEUE_PREFIX + queueName;
        String messageText = "";
        String attributeText="";

        ReceiveMessageRequest rmr=new ReceiveMessageRequest()
                .withQueueUrl( queueUrl )
                .withMessageAttributeNames(ALL_ATTRIBUTES);



//        List<Message> messages = sqsClient.receiveMessage( queueUrl ).getMessages();
        List<Message> messages = sqsClient.receiveMessage( rmr ).getMessages();
        for (Message m : messages) {

            // if attribute is present then show, but otherwise show "no attribute"
            attributeText = m.getMessageAttributes().containsKey(ATTRIBUTE_NAME)
                    ? m.getMessageAttributes().get(ATTRIBUTE_NAME).getStringValue() : "no value";

            messageText = m.getBody();
            System.out.println( "Message body was : " + messageText + " -> " + ATTRIBUTE_NAME + " : " + attributeText );

            sqsClient.deleteMessage(queueUrl, m.getReceiptHandle());
        }

        return messageText;
    }








}
