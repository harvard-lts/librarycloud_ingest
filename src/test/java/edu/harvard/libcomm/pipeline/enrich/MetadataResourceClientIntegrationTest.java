/**
 *  Copyright (c) 2019 by the President and Fellows of Harvard College
 */
package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.pipeline.Config;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Integration test which accesses a local Servlet container (Tomcat) instance with this application deployed.
 * The 'delete' methods access Mongo directly.
 * For ID's that don't exist in Mongo, a DRS Services instance is also required.
 * Always make sure the @Ignore annotation is on the class declaration.
 *
 * @author dan179
 */

public class MetadataResourceClientIntegrationTest {

    private static ResteasyClient resteasyClient;

    private static final String OBJECT_METADATA_ENDPOINT = "/drs_metadata/rest/metadata/object";
    private static final String FILE_METADATA_ENDPOINT = "/drs_metadata/rest/metadata/file";
    // default expiration time beyond "now" in milliseconds for JWT's
    private static long DEFAULT_EXPIRATION = 1000 * 60 * 5; // millis * seconds & minutes
    protected Logger log = Logger.getLogger(MetadataResourceClientIntegrationTest.class);

    @BeforeAll
    public static void initAll() {
        resteasyClient = new ResteasyClientBuilder().connectionPoolSize(10).maxPooledPerRoute(10).build();
    }

    @Test
    public void testLibraryCloudObject() {
        //int objectId = 462479396; //deadletter
        //int objectId = 473172644;
        int objectId = 400338692; //PDS
        //int objectId = 400338492; //IDS
        //int objectId = 400087249; //SDS (multiple, also has FDS
        //int objectId = 400333792; //FDS;
        //int objectId = 400086911; //PDS_LIST
        //int objectId = 400333866; //SDS_VIDEO
        //int objectId = 462381088; //PDS PROD 462315836 (normal), 462381088 errors
        //int objectId = 400004554; //FDS R flag, dev
        //int objectId = 400000100; //FDS N flag, dev
        //int objectId = 472309868; //SDS restricted prod

        log.info("here we go...");
        String serverUrl = Config.getInstance().INTEGRATION_SERVER_URL;
        if (serverUrl == null) {
            fail("No property [integration.server.url] for integration server URL:");
        }

        String endpoint = serverUrl + OBJECT_METADATA_ENDPOINT;
        log.info("About to access Tomcat at this URL endpoint: {}: " + endpoint);

        String lcKey = Config.getInstance().JWT_LC_KEY;
        log.info("lcKey: " + lcKey);
        byte[] keyBytes = Base64.getDecoder().decode(lcKey.getBytes());
        Key decodedKey = Keys.hmacShaKeyFor(keyBytes);

        JwtBuilder builder =  getBasicJwtBuilder("LC", objectId);
        builder.signWith(decodedKey);
        String jws = builder.compact();
        log.info("jws: " + jws);
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
            fail("Did not get expected response code. Got: " + response.getStatus());
        }

        Object obj = response.readEntity(Object.class);
        log.info("Response Object Entity: {}: " + obj);
    }

    @Test
    public void testLibraryCloudFile() {

        int drsId = 400122526;  // file ID
        //int drsId = 400121094;
        // files: 400122526
//		int drsId = 400263492; (file)400062398 - access: N  400034132 - 259 files deleted; 400121690 - 1 obj, 3 files

        log.info("here we go...");
        String serverUrl = Config.getInstance().INTEGRATION_SERVER_URL;
        if (serverUrl == null) {
            fail("No property [integration.server.url] for integration server URL:");
        }

        String endpoint = serverUrl + FILE_METADATA_ENDPOINT;
        log.info("About to access Tomcat at this URL endpoint: {}: " + endpoint);

        String lcKey = Config.getInstance().JWT_LC_KEY;
        log.info("lcKey: " + lcKey);
        byte[] keyBytes = Base64.getDecoder().decode(lcKey.getBytes());
        Key decodedKey = Keys.hmacShaKeyFor(keyBytes);

        JwtBuilder builder =  getBasicJwtBuilder("LC", drsId);
        builder.signWith(decodedKey);
        String jws = builder.compact();
        log.info("jws: " + jws);
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
            fail("Did not get expected response code. Got: " + response.getStatus());
        }

        Object obj = response.readEntity(Object.class);
        log.info("Response Object Entity: {}: " + obj);
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

}
