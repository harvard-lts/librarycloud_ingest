package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.pipeline.Config;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

public class DrsMdsProcessor implements Processor {

    private static ResteasyClient resteasyClient;

    private static final String OBJECT_METADATA_ENDPOINT = "/drs_metadata/rest/metadata/object";
    private static final String FILE_METADATA_ENDPOINT = "/drs_metadata/rest/metadata/file";
    // default expiration time beyond "now" in milliseconds for JWT's
    private static long DEFAULT_EXPIRATION = 1000 * 60 * 5; // millis * seconds & minutes
    protected Logger log = Logger.getLogger(DrsMdsProcessor.class);

    public void process(Exchange exchange) throws Exception {

        String drsIdStr = exchange.getIn().getBody(String.class);
        int drsId = 0;
        try {
            drsId =Integer.parseInt(drsIdStr);
            log.info("Here's the objectId passed from activemq: " + drsId);
        }
        catch (NumberFormatException e) {
            log.error("drs id: " + drsIdStr + " not parseable to integer");
        }
        log.info("here we go...");
        String serverUrl = Config.getInstance().INTEGRATION_SERVER_URL;
        if (serverUrl == null) {
            log.error("No property [integration.server.url] for integration server URL:");
        }

        String endpoint = serverUrl + OBJECT_METADATA_ENDPOINT;
        //String endpoint = serverUrl + FILE_METADATA_ENDPOINT;
        log.info("About to access Tomcat at this URL endpoint: {}: " + endpoint);

        String lcKey = Config.getInstance().JWT_LC_KEY;
        //log.info("lcKey: " + lcKey);
        byte[] keyBytes = Base64.getDecoder().decode(lcKey.getBytes());
        Key decodedKey = Keys.hmacShaKeyFor(keyBytes);

        JwtBuilder builder =  getBasicJwtBuilder("LC", drsId);
        builder.signWith(decodedKey);
        String jws = builder.compact();
        //log.info("jws: " + jws);
        // parse the signed JWS and show it as JWT
        String key = Config.getInstance().JWT_LC_KEY;
        @SuppressWarnings("unchecked")
        Jwt<?, Claims> jwt = Jwts.parser().setSigningKey(key)
                .setAllowedClockSkewSeconds(DEFAULT_EXPIRATION)
                .parse(builder.compact());
        log.info("JWT: {} " + jwt);


        Entity<String> jwtEntity = Entity.entity(jws, MediaType.TEXT_PLAIN_TYPE);

        ResteasyWebTarget target = resteasyClient.target(endpoint);
        Response response = target.request().post(jwtEntity);

        if (response.getStatus() != HttpStatus.SC_OK
                && response.getStatus() != HttpStatus.SC_ACCEPTED) {
            log.error("Did not get expected response code. Got: " + response.getStatus());
        }

        Object obj = response.readEntity(Object.class);
        //log.info("Response Object Entity: {}: " + obj);
        //log.info("Passing result from DMS to lc ingest for solr mapping and ingest");
        exchange.getIn().setBody(obj);

        /*
        String url = Config.getInstance().MARC_S3_URL + "/" + almaId;
        url = url.trim();
        System.out.println("url: " + url);
        URI uri = new URI(url);
        String marcxml = "";
        try {
            marcxml = IOUtils.toString(uri.toURL().openStream(), "UTF-8");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        LibCommMessage message = new LibCommMessage();
        message.setCommand("NORMALIZE");
        LibCommMessage.Payload payload = new LibCommMessage.Payload();
        payload.setSource("ALMA");
        payload.setFormat("mods");
        payload.setData(marcxml);
        message.setPayload(payload);
        String marshalledMessage = MessageUtils.marshalMessage(message);
        exchange.getIn().setBody(marshalledMessage);
        */
    }

    private JwtBuilder getBasicJwtBuilder(String serviceId, int lookupId) {

        JwsHeader<?> header = Jwts.jwsHeader();
        header.setKeyId(serviceId);

        long timeUntilExpirationMillis = 1000 * 60 * 30; // millis * seconds & minutes

        JwtBuilder jwt = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + timeUntilExpirationMillis))
                .claim("lookup_id", String.valueOf(lookupId))
                .setHeader((Map<String, Object>)header);

        return jwt;
    }

    public DrsMdsProcessor() {
        resteasyClient = new ResteasyClientBuilder().connectionPoolSize(10).maxPooledPerRoute(10).build();
    }
}
