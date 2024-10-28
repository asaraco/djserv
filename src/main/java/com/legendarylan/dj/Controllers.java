package com.legendarylan.dj;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;

import com.legendarylan.dj.vdj.controller.VDJNetController;
import com.legendarylan.dj.vdj.controller.XmlController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin({"http://${app.legendarydj.localhost-ip}:8080", "http://${app.legendarydj.localhost-ip}:4200", "http://localhost:4200"})
public class Controllers {
	private static Logger logger = LogManager.getLogger(Controllers.class);

	// Injection
	private final VDJNetController vdjNetController;
	private final XmlController xmlController;

	/**
	 * Constructor.
	 * @param vdjNetController - Injected to provide access to the other controller class
	 */
	public Controllers(VDJNetController vdjNetController, XmlController xmlController) {
		this.vdjNetController = vdjNetController;
        this.xmlController = xmlController;
    }
	
	/* Get application properties */
	@Value("${app.legendarydj.file-path}")
	private String filePath;
	@Value("${app.legendarydj.mode}")
	private String mode;
	
	private static ArrayList<Integer> allAssignedIDs = new ArrayList<Integer>();
	static {
		allAssignedIDs.add(0);
	}

	@GetMapping("/hello")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
      return String.format("Hello %s!", name);
    }
	
	@PostMapping("/upload")
	public ResponseEntity<?> handleFileUpload( @RequestParam("song") MultipartFile file ) {
		String fileContentType = file.getContentType().toLowerCase();
		String fileName = file.getOriginalFilename();
		if (file.isEmpty() || file.getSize()==0) {
			return ResponseEntity.ok("{\"message\": \"ERROR - File appears to be empty - " + fileName + "\"}");
		} else if (!Const.FILE_TYPES_VALID.contains(fileContentType)) {
			logger.error("File rejected: Unexpected content type {}", fileContentType);
			return ResponseEntity.ok("{\"message\": \"ERROR - File did not match an accepted extension - " + fileName + "\"}");
		} else {
			try {
				file.transferTo(new File(filePath + fileName));
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
			logger.info("File uploaded: {}{}", filePath, fileName);
			// AMS 10/27/2024 - Trying a server-side library update since client-side is harder to sync
			if (mode.equalsIgnoreCase("vdj")) {
				ResponseEntity<String> result = (ResponseEntity<String>) this.vdjNetController.refreshSongBrowser();
				if (result.getBody().equalsIgnoreCase("true")) {
					logger.info("Finished VDJ browser refresh script");
					this.xmlController.forceReloadDatabase();
					logger.info("Finished VDJ database reload");
				} else {
					logger.info("Refresh didn't work, sorry");
				}
			}
			// Return
			return ResponseEntity.ok("{\"message\": \"File uploaded successfully - " + fileName + "\"}");
		}
	}
	
	/**
	 * Generate a random "User ID" number.
	 * # of loops is constrained by the maximum # of the RNG.
	 * NOTE: If by some chance it generates enough numbers that none are left,
	 * it will simply return whichever random # it lands on.
	 * Obviously with a high enough number, this should never happen.
	 * @return
	 */
	@GetMapping("/generateRandomID")
	public static Integer generateRandomID() {
		//System.out.println("Existing IDs: " + allAssignedIDs);
		
		Integer randomNumber = 0;
		SecureRandom rng = new SecureRandom();
		Integer limit = 5000;		
		Integer loopCounter = 0;
		Integer maxLoops = limit - allAssignedIDs.size();
		
		while (allAssignedIDs.contains(randomNumber) && (loopCounter <= maxLoops)) {
			loopCounter++;
			randomNumber = (Integer)rng.nextInt(limit);
		}
		
		allAssignedIDs.add(randomNumber);
		
		logger.info("Assigning User ID #: {}", randomNumber);
		return randomNumber;
	}

}
