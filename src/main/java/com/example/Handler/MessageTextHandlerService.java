package com.example.Handler;

/*
    Want to look at a possible variety of ways to handle what's in the queue.  Handlers might:
        kick on to a restful endpoint
        persist in a database
        generate an email
        send on to a further queue
        etc

    ?? Do we want a collection of Handlers to be passed in to a poller/listener ??
    Probably not a brilliant idea, given at some point someone will want to decide if a message is done (and can be removed from the queue)
 */
public interface MessageTextHandlerService {

    // For the time being just return -ve = unhandleable, 0 = unhandled, +ve = handled
    public int handle(String msgText);

}
