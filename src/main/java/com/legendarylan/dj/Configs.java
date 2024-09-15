package com.legendarylan.dj;

import com.legendarylan.dj.vdj.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
@EnableCaching
public class Configs {
	private static Logger logger = LogManager.getLogger(Configs.class);

	@Bean
	@ConditionalOnProperty(prefix = "app.legendarydj", name = "mode", havingValue = "mixxx")
	public CacheManager cacheManager() {
		logger.info("***** CACHE MANAGER INITIALIZING *****");
		return new ConcurrentMapCacheManager("library");
	}

	@Bean
	@ConditionalOnProperty(prefix = "app.legendarydj", name="mode", havingValue="vdj")
	public CacheManager cacheManagerVDJ() {
		logger.info("***** VDJ CACHE MANAGER INITIALIZING *****");
		return new ConcurrentMapCacheManager("vdjDatabase");
	}

	@Bean
	public Jaxb2Marshaller marshaller(){
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		marshaller.setClassesToBeBound(VirtualDJDatabase.class, Track.class, Tags.class, VirtualFolder.class, PlaylistSong.class);
		return marshaller;
	}

}
