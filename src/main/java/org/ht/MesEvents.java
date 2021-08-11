package org.ht;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.kafka.api.IncomingKafkaRecordMetadata;

import java.util.concurrent.CompletionStage;

import javax.enterprise.context.ApplicationScoped;



@Path("/hello")
@ApplicationScoped
public class MesEvents {
    public static Logger logger = LoggerFactory.getLogger(MesEvents.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @Incoming("mesevents")
    public CompletionStage<Void> consume(Message<String> msg) {
    // access record metadata
    var metadata = msg.getMetadata(IncomingKafkaRecordMetadata.class).orElseThrow();
    // process the message payload.
    String eventmsg = msg.getPayload();
    logger.info("Event message:"+eventmsg);
    // Acknowledge the incoming message (commit the offset)
    return msg.ack();
}
}