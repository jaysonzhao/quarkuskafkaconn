package org.ht;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;
import io.smallrye.reactive.messaging.annotations.Broadcast;
import io.smallrye.reactive.messaging.annotations.Channel;

import org.jboss.resteasy.annotations.SseElementType;
import javax.enterprise.context.ApplicationScoped;
import org.reactivestreams.Publisher;
import javax.inject.Inject;


@Path("/ui-topic")
@ApplicationScoped
public class MesEvents {
    public static Logger logger = LoggerFactory.getLogger(MesEvents.class);

    @Inject
    @Channel("data-stream") Publisher<String> message;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello";
    }
    
    @GET
    @Path("/stream")
    @Produces(MediaType.SERVER_SENT_EVENTS)
   // @SseElementType("text/plain") 
    public Publisher<String> stream() {
        logger.info("Sending front message");
        return message;
    }

    @Incoming("mesevents")
    @Outgoing("data-stream")
    @Broadcast
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public String consume(Message<String> msg) {
    // access record metadata
    var metadata = msg.getMetadata(IncomingKafkaRecordMetadata.class).orElseThrow();
    // process the message payload.
    String htmlOutput = "";
    String eventmsg = msg.getPayload();
    logger.info("Event message:"+eventmsg);
    htmlOutput += "<div><h3>Message:&nbsp<b>" + eventmsg + "</b></h3></div>";
    // Acknowledge the incoming message (commit the offset)
    return htmlOutput.replaceAll("\"", "");
    }
    
    
    public String processevent(String msg){
        String htmlOutput = "";
        
        logger.info("Sending frontend message:"+ msg);
        htmlOutput += "<div><h3>Message:&nbsp<b>" + msg + "</b></h3></div>";
        // Acknowledge the incoming message (commit the offset)
        return htmlOutput.replaceAll("\"", "");
    }

}