package ua.com.epam.core.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.testng.Assert;
import ua.com.epam.config.DataProp;
import ua.com.epam.entity.Response;
import ua.com.epam.utils.helpers.LocalDateAdapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

public class RestClient {
    private static Logger log = Logger.getLogger(RestClient.class);

    //custom Response object (wrapper)
    private Response response;

    //build here our HTTPClient to execute needed requests
    private HttpClient client = HttpClientBuilder.create().build();
    private Gson g = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).create();

    public Response getResponse() {
        return this.response;
    }

    //get just take only URI but as URIBuilder object
    public void get(String uri) {
        //create exactly get http request. It assume String with URI
        HttpGet request = new HttpGet(uri);

        //set header. Here we say to API, that we expect especially JSON in response
        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        //here we create HttpResponse object
        HttpResponse response = null;
        try {
            log.info("Perform GET request to: " + request.getURI().toString());
            //execute request and write response from
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            log.error("HTTP protocol error!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Some problems occur or the connection was aborted!");
            e.printStackTrace();
        }

        //and the last: wrap HttpResponse to our custom Response object (look at line 145)
        wrapResponse(response);
    }

    //post take URI but as URIBuilder object and some object to post
    public void post(String uri, Object body) {
        HttpPost request = new HttpPost(uri);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        String reqB = g.toJson(body);

        HttpResponse response = null;
        try {
            StringEntity bodyToPost = new StringEntity(reqB);
            request.setEntity(bodyToPost);

            log.info("Perform POST request to: " + request.getURI().toString());
            response = client.execute(request);
        } catch (UnsupportedEncodingException e) {
            log.error("The Character Encoding is not supported!");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            log.error("HTTP protocol error!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Some problems occur or the connection was aborted!");
            e.printStackTrace();
        }

        wrapResponse(response);
    }

    //put take URI but as URIBuilder object and some object to put
    public void put(String uri, Object body) {
        HttpPut request = new HttpPut(uri);
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");

        String reqB = g.toJson(body);

        HttpResponse response = null;
        try {
            StringEntity bodyToPut = new StringEntity(reqB);
            request.setEntity(bodyToPut);

            log.info("Perform PUT request to: " + request.getURI().toString());
            response = client.execute(request);
        } catch (UnsupportedEncodingException e) {
            log.error("The character encoding is not supported!");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            log.error("HTTP protocol error!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Some problems occur or the connection was aborted!");
            e.printStackTrace();
        }

        wrapResponse(response);
    }

    //delete take URI as URIBuilder object
    public void delete(String uri) {
        HttpDelete request = new HttpDelete(uri);
        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        HttpResponse response = null;

        try {
            log.info("Perform DELETE request to: " + request.getURI().toString());
            response = client.execute(request);
        } catch (ClientProtocolException e) {
            log.error("HTTP protocol error!");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Some problems occur or the connection was aborted!");
            e.printStackTrace();
        }

        wrapResponse(response);
    }

    private void wrapResponse(HttpResponse response) {
        // here we check if we not catch some exceptions while colling API
        // just verify our response on null value
        if (response == null) {
            Assert.fail("Response is empty!");
        }

        //get status code from our HttpResponse object
        int statusCode = response.getStatusLine().getStatusCode();

        //and also read our response body
        HttpEntity entity = response.getEntity();

        //check if response body is not null (e. g. after delete )
        if (entity == null) {
            //will write response to custom object Response (inside RestClient object)
            this.response = new Response(statusCode, "");
            return;
        }

        String body = "";

        try {
            body = EntityUtils.toString(entity, "UTF-8");
        } catch (ParseException e) {
            log.error("Header elements cannot be parsed!");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.response = new Response(statusCode, body);
    }
}
