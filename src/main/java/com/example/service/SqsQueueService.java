package com.example.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

//@Service
//@Qualifier("rawQueue")
public class SqsQueueService implements Runnable{

    boolean poll = true;




//    @EventListener(ApplicationStartedEvent.class)
//    public void handleApplicationStarted(){
//        System.out.println("Application Started according to listener.  in QueueService");
//    }

    // Will need sqs functionality - handled by Config
    @Autowired
//    AmazonSQSClient sqsClient;
    AmazonSQS sqsClient;



    String ALL_ATTRIBUTES = "All";  // VERY case sensitive !!!
    String ATTRIBUTE_NAME = "txID";
    String QUEUE_PREFIX = "queue/";
    Integer LONG_POLL = 5;  // how long to wait if nothing returned
    Integer MAX_MSGS =2;    // how many messages at a time




    // Probably best to try and avoid creating queue too many times
    String forQueue = null;




    // Check creation of consumer with details
    public SqsQueueService(String queueName) {
        System.out.println("QueueService instanced with " + queueName );
        forQueue = queueName;
    }

    // Check creation of consumer with details
    public SqsQueueService() {
        System.out.println("QueueService instanced with " + "<NO QUEUE NAME GIVEN>" );
    }


    @Override
    public void run() {
        // bare minimum to make this start
        doSQS();

        pause();
        beginPollinOnCommand();

    }

    // Moved this out of run while I experiment
    public void beginPollinOnCommand() {

        // Poll (what if things aren't ready for our polling?
//        System.out.println("pre poll pause");
//        pause();

        System.out.println("starting the polling in beginPollinOnCommand");
        while (poll) {
            String msgTxt = getMessage(forQueue);
            pause();
        }
    }

    private void pause() {
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }








    /**
     * initial point to instance things
     */
    public void doSQS() {
        // Should maybe offer mechanism to create a queue (or set of)
        System.out.println("doSQS called");
    }

    /**
     * Just attempt the queue creation regardless for now, but should be able to check the existing
     * @param queueName
     */
    protected void createQueue( String queueName ) {

        // We should have set the queue if it's there (including oif we just created it)
        if (forQueue == null){

            CreateQueueRequest createRequest=new CreateQueueRequest( queueName )
                    .addAttributesEntry("MessageRetentionPeriod", "86400");

            try {
                // Use SQS create queue and record name if we don't throw an exception
                sqsClient.createQueue(createRequest);
                forQueue = queueName;

            } catch (Exception e) {
                e.printStackTrace();
            }

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
                        .withStringValue("test attrib value for " + messageText))
                ;

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

        // BB 20200419 - atempting to run as poller is failing as queue needn't exist
        createQueue( queueName );

        ReceiveMessageRequest rmr=new ReceiveMessageRequest()
                .withQueueUrl( queueUrl )
                .withMessageAttributeNames(ALL_ATTRIBUTES)
                .withWaitTimeSeconds(LONG_POLL)    // delay before next poll IF nothing returend
                .withMaxNumberOfMessages(MAX_MSGS) // Number of messages per poll
                ;



//        List<Message> messages = sqsClient.receiveMessage( queueUrl ).getMessages();
        List<Message> messages = sqsClient.receiveMessage( rmr ).getMessages();
        System.out.println( "Messages retrieved : " + messages.size() );

        for (Message m : messages) {

            // if attribute is present then show, but otherwise show "no attribute"
            attributeText = m.getMessageAttributes().containsKey(ATTRIBUTE_NAME)
                    ? m.getMessageAttributes().get(ATTRIBUTE_NAME).getStringValue() : "no value";

            messageText = m.getBody();
            System.out.println( "Message body was : " + messageText + " -> " + ATTRIBUTE_NAME + " : " + attributeText );


            // This is where we will want to put in stuff to kick this upstairs
            // which could be directly OR via some kind of exchange

            // ? Where would any unencryption sit?


            // Likely to want to react on basis of response to the publish/handle stuff
            sqsClient.deleteMessage(queueUrl, m.getReceiptHandle());
        }

        return messageText;
    }

    // While I play!!
    public String getQueueName() {
        System.out.println("getQueueName returning -> " + forQueue);
        return forQueue;
    }







}
