package com.legendarylan.dj.vdj.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Class controlling how scripts are sent to the
 * VirtualDJ Network Controller and executed.
 */
public class VDJNetworkControlInterface {
    static Logger logger = LogManager.getLogger(VDJNetworkControlInterface.class);

    private static final String queryUri = "/query";
    private static final String execUri = "/execute";

    /**
     * Replace illegal characters typically found in a file path
     * with ones that can be sent in a request
     * to the VDJ Network Controller.
     * @param dirtyPath The un-sanitized path string
     * @return Sanitized path string
     */
    public static String sanitizePath(String dirtyPath) {
        String sanitizedPath = dirtyPath.replace(":", "%3A");
        sanitizedPath = sanitizedPath.replace("/", "%2F");
        sanitizedPath = sanitizedPath.replace("\\", "%5C");
        return sanitizedPath;
    }

    /**
     * Replace illegal characters found in a VDJ script command
     * with ones that can be sent in a request
     * to the VDJ Network Controller.
     * @param dirtyScript The un-sanitized script string
     * @return Sanitized script string
     */
    public static String sanitizeScript(String dirtyScript) {
        String sanitizedScript = dirtyScript.replace("&", "%26");
        sanitizedScript = sanitizedScript.replace("\"", "%22");
        sanitizedScript = sanitizedScript.replace(" ", "%20");
        return sanitizedScript;
    }

    /**
     * Prepare and send a script to the VDJ Network Controller.
     * @param actionPath Typically '/execute' or '/query'
     * @param baseUri Address of the network controller
     * @param scriptBody The actual script
     * @param token Token that authenticates this app to the VDJ Network Controller
     * @return The response from the VDJ Network Controller
     */
    public static String doScript(String actionPath, String baseUri, String scriptBody, String token) {
        String method = "doScript";
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

    /**
     * Invoke the doScript() method setting the action path to '/execute'
     * @param baseUri Address of the network controller
     * @param scriptBody The actual script
     * @param token Token that authenticates this app to the VDJ Network Controller
     * @return The response from the VDJ Network Controller
     */
    public static String doScriptExec(String baseUri, String scriptBody, String token) {
        return doScript(execUri, baseUri, scriptBody, token);
    }

    /**
     * Invoke the doScript() method setting the action path to '/query'
     * @param baseUri Address of the network controller
     * @param scriptBody The actual script
     * @param token Token that authenticates this app to the VDJ Network Controller
     * @return The response from the VDJ Network Controller
     */
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
