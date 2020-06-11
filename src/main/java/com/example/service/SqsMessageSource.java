package com.example.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.example.Handler.MessageTextHandlerService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

//@Service
//@Qualifier("rawQueue")
public class SqsMessageSource implements Runnable, MessageSource {

    boolean poll = true;


    // Will need sqs functionality - handled by sqsClient (via config)
    @Autowired
    AmazonSQS sqsClient;

    // Could (but maybe not should) autowire validator, crypto, mapper, etc



    private String ALL_ATTRIBUTES = "All";  // VERY case sensitive !!!
    private String ATTRIBUTE_NAME = "txID";
    private String QUEUE_PREFIX = "queue/";
    private Integer LONG_POLL = 5;  // how long to wait if nothing returned
    private Integer MAX_MSGS = 2;    // how many messages at a time
    private boolean CREATE_Q_ALWAYS = false;    // Create queue called every time we do a get / send ??

    // Want something to handle the queue entries we retrieve
    MessageTextHandlerService messageHandlerService;

    // === SET UP / CONFIG TYPE STUFF ===
    // Queues need names and queue Message Sources need queues
//    String forQueue = null;

    String qNme = null;
    String qUrl = null;

    // At this point should have enough details (specifically sqsClient should be good to go)
    @PostConstruct
    private void doInit() {
        // Fire once (rather than every time we want to create/read message)
        createQueue();
//        deleteQueue();
    }

    // Not much use being a queue poller if we don't have a queue
    public SqsMessageSource(String queueName) {
//        System.out.println(">>>> QueueService instanced with " + queueName);

        qNme = queueName;
        qUrl = QUEUE_PREFIX + qNme;
    }

    // Receiving a message is pointless without a mean to do something with it
    public void setMessageHandler(MessageTextHandlerService messageHandlerService) {
        this.messageHandlerService = messageHandlerService;
    }

    // === RUNNABLE ===
    @Override
    public void run() {
        // pause for a second and then start polling/handling
        pause();
        beginPollingOnCommand();
    }

    // Moved this out of run while I experiment
    public void beginPollingOnCommand() {

//        System.out.println("starting the polling in beginPollinOnCommand");
        while (poll) {
            getMessages();
            pause();
        }
    }

    // Avoid zillions of try blocks
    private void pause() {
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the queue (should be idempotent, so matters little if queue already exists)
     *
     */
    protected void createQueue() {
        CreateQueueRequest createRequest = new CreateQueueRequest(qNme)
                .addAttributesEntry("MessageRetentionPeriod", "86400");
        try {
            sqsClient.createQueue(createRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Delete the queue.
    protected void deleteQueue() {
        DeleteQueueRequest deleteQueueRequest = new DeleteQueueRequest(qUrl);
        try {
            sqsClient.deleteQueue(deleteQueueRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getQueueUrl() {
        return qUrl;
    }



    /**
     * Given message and where to write it, do so
     *
     * @param messageText
     */
//    public void sendMessage(String messageText, String queueName) {
    public void sendMessage(String messageText) {

        // Whilst I check the deal with need for queue creation
        if (CREATE_Q_ALWAYS) createQueue();

        // Generate the request.  Must be a better way than having queue as text in the calls
        SendMessageRequest smr = new SendMessageRequest()
                .withQueueUrl(qUrl)
                .withMessageBody(messageText)
                .withDelaySeconds(0)
                // add attributes - why ??
                .addMessageAttributesEntry(ATTRIBUTE_NAME,
                        new MessageAttributeValue()
                                .withDataType("String")
                                .withStringValue("test attrib value for " + messageText)
                );

        // send request to client
        sqsClient.sendMessage(smr);
    }

    /**
     * Get the Messages which will be picked up by the handler we've been given
     */
    public void getMessages() {

        // Whilst I check the deal with need for queue creation
        if (CREATE_Q_ALWAYS) createQueue();

        ReceiveMessageRequest rmr = new ReceiveMessageRequest()
                .withQueueUrl(qUrl)
                .withMessageAttributeNames(ALL_ATTRIBUTES)
                .withWaitTimeSeconds(LONG_POLL)    // delay before next poll IF nothing returend
                .withMaxNumberOfMessages(MAX_MSGS) // Number of messages per poll
                ;

        // send request to client and receive response as list of messages
        List<Message> messages = sqsClient.receiveMessage(rmr).getMessages();
        System.out.println("Messages retrieved " + qNme + ": " + messages.size());

        // Pass the messages off to be handled (one at a time)
        handleMessages( messages );
    }

    // For now we'll work on the given handler returning whether its handled/unhandled/unhandlable (latter = DLQ candidate)
    protected void handleMessages(List<Message> messages) {
        int code=0;
        String messageText = "";
        String attributeText = "";

        for (Message m : messages) {

            // if attribute is present then show, but otherwise show "no attribute"
            attributeText = m.getMessageAttributes().containsKey(ATTRIBUTE_NAME)
                    ? m.getMessageAttributes().get(ATTRIBUTE_NAME).getStringValue() : "no value";

            messageText = m.getBody();
//            System.out.println("Message body was : " + messageText + " -> " + ATTRIBUTE_NAME + " : " + attributeText);


            // Payload
            code = handleMessageText(messageText) ;
            System.out.println("handler returned code = " + code);

            // If it can't be handled ever (dodgy data)
            if ( code < 0 ) {
                // Will want to stick on a DLQ
            }

            // If it's unhandled or not handleable then effectively do an ACK. Remove from queue
            if ( code != 0 ) {
                sqsClient.deleteMessage(qUrl, m.getReceiptHandle());
            }
        }
    }

    /*
        ?? Where would we see the encryption / validation / mapping / ect sit ??
        Different sources likely to have different credentials, encryption, message structure, validations etc
     */
    protected int handleMessageText(String messageText) {
        int code=0;

        // Still not sure if the crypto/validation/etc sits here ...


        // Likely to want to react on basis of response to the publish/handle stuff
        if (messageHandlerService != null) {
            code = messageHandlerService.handle(messageText);
        }

        // Pass back handling info
        return code;
    }



    // While I play!!
    public String getQueueName() {
        System.out.println("getQueueName returning -> " + qNme);
        return qNme;
    }

    // Need to add some kind of mechanism to make sure we don't start in zillions of threads
    @Override
    public void begin() {
        System.out.println("it begins -> " + qNme);
        Thread t = new Thread(this);
        t.start();
    }

    // Stop producing messages altogether
    @Override
    public void cease() {
        System.out.println("Cease called");
    }

    // Mechanism to temporarily halt/unhalt production of messages


}
