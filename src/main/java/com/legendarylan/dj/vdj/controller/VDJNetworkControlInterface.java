package com.legendarylan.dj.vdj.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class VDJNetworkControlInterface {
    static Logger logger = LogManager.getLogger(VDJNetworkControlInterface.class);

    private static final String queryUri = "/query";
    private static final String execUri = "/execute";

    public static String sanitizePath(String dirtyPath) {
        String sanitizedPath = dirtyPath.replace(":", "%3A");
        sanitizedPath = sanitizedPath.replace("/", "%2F");
        sanitizedPath = sanitizedPath.replace("\\", "%5C");
        return sanitizedPath;
    }

    public static String sanitizeScript(String dirtyScript) {
        String sanitizedScript = dirtyScript.replace("&", "%26");
        sanitizedScript = sanitizedScript.replace("\"", "%22");
        sanitizedScript = sanitizedScript.replace(" ", "%20");
        return sanitizedScript;
    }

    public static String doScript(String actionPath, String baseUri, String scriptBody, String token) {
        String method = "executeScript";
        logger.trace("{}: ENTER", method);
        // Call VDJ Network Control Plugin
        RestTemplate restTemplate = new RestTemplate();
        // The below is necessary for proper encoding.
        String sanitizedScript = sanitizeScript(scriptBody);
        UriComponents myUri = UriComponentsBuilder.fromHttpUrl(baseUri)
                .path(actionPath)
                .queryParam("script",sanitizedScript)
                .queryParam("bearer",token)
                .build();
        logger.debug("{}: BUILT URI - {}", method, myUri);
        URI converted = URI.create(myUri.toString());
        // Do the call
        String result = restTemplate.getForObject(converted, String.class);
        logger.debug("{}: RESULT - {}", method, result);
        // Finish
        return result;
    }

    public static String doScriptExec(String baseUri, String scriptBody, String token) {
        return doScript(execUri, baseUri, scriptBody, token);
    }

    public static String doScriptQuery(String baseUri, String scriptBody, String token) {
        return doScript(queryUri, baseUri, scriptBody, token);
    }

    public static int getTimeRemaining(String baseUri, String token) {
        // Call VDJ Network Control Plugin
        RestTemplate restTemplate = new RestTemplate();
        String scriptBody = "deck active get_time remain";
        logger.debug(scriptBody);
        // Do the call
        String result = doScriptQuery(baseUri, scriptBody, token);
        System.out.println(result);
        return Integer.parseInt(result);
    }

    public static double getSongPosition(String baseUri, String token) {
        // Call VDJ Network Control Plugin
        RestTemplate restTemplate = new RestTemplate();
        String scriptBody = "deck active get_position";
        logger.debug(scriptBody);
        // Do the call
        String result = doScriptQuery(baseUri, scriptBody, token);
        System.out.println(result);
        return Double.parseDouble(result);
    }
}
