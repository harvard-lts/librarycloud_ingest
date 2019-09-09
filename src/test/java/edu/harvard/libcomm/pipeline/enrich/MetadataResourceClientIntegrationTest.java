/**
 *  Copyright (c) 2019 by the President and Fellows of Harvard College
 */
package edu.harvard.libcomm.pipeline.enrich;

import edu.harvard.libcomm.pipeline.Config;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;

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

        int drsId = 400062398;
//		int drsId = 400263492; 400062398 - access: N  400034132 - 259 files deleted; 400121690 - 1 obj, 3 files

        log.info("here we go...");
        String serverUrl = Config.getInstance().INTEGRATION_SERVER_URL;
        if (serverUrl == null) {
            fail("No property [integration.server.url] for integration server URL:");
        }

        CloseableHttpClient client = HttpClients.createDefault();
        String endpoint = serverUrl + "/drs-metadata/rest/metadata/object";
        log.debug("About to access Tomcat at this URL endpoint: {}: " + endpoint);

        String lcKey = Config.getInstance().JWT_LC_KEY;
        byte[] keyBytes = Base64.getDecoder().decode(lcKey.getBytes());
        Key decodedKey = Keys.hmacShaKeyFor(keyBytes);

        JwtBuilder builder =  getBasicJwtBuilder("LC", drsId);
        builder.signWith(decodedKey);
        String jws = builder.compact();

        HttpPost httpPost = new HttpPost(endpoint);
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("jwt", jws));

        CloseableHttpResponse response = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps));
            response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            log.info("response status: {}: " + statusLine);
            log.info("response: {}: " + response.getEntity().getContentType());
            if (statusLine.getStatusCode() != HttpStatus.SC_OK) {
                fail("Did not get expected response code. Got: " + statusLine.getStatusCode());
            }

            HttpEntity entity = response.getEntity();
            StringWriter writer = new StringWriter();
            IOUtils.copy(entity.getContent(), writer, StandardCharsets.UTF_8);
            String theString = writer.toString();
            log.info("returned text: {}: " + theString);
            // do something useful with the response body
            // and ensure it is fully consumed
            EntityUtils.consume(entity);
        } catch (Exception e) {
            log.error("uh oh...", e);
            fail(e.getMessage());
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Test
    public void testLibraryCloudFile() {

        //resteasyClient = new ResteasyClientBuilder().connectionPoolSize(10).maxPooledPerRoute(10).build();
        int drsId = 400122526; // file ID
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
