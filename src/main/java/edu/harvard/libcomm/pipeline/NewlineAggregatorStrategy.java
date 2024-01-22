package edu.harvard.libcomm.pipeline;

import org.apache.camel.Exchange;
import org.apache.camel.AggregationStrategy;
//import org.apache.camel.processor.aggregate.CompletionAwareAggregationStrategy;
import org.apache.log4j.Logger;

/**
 * Created by mjv162 on 1/22/2019.
 */
public class NewlineAggregatorStrategy implements AggregationStrategy {

        protected Logger log = Logger.getLogger(MARCXMLRawAggregatorStrategy.class);
        private String source;

        /**
         * Combine two LibComMessages into a new LibComMesasge with the payload data concatenated
         * @param  oldExchange message 1
         * @param  newExchange message 2
         * @return             updated message
         */
        @Override
        public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        String oldBody = oldExchange.getIn().getBody(String.class);
        String newBody = newExchange.getIn().getBody(String.class);
        String body = oldBody + "\n" + newBody;
        oldExchange.getIn().setBody(body);

        /* Set the approx. size of the message in a header so the information can
           be used to keep message size below AWS 256k limit */
        oldExchange.getIn().setHeader("messageLength", body.length());

        return oldExchange;
    }
}
