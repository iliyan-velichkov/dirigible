package org.eclipse.dirigible.components.initializers.synchronizer;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * The Class SynchronizationWatcher.
 */
@Component
@Scope("singleton")
public class SynchronizationWatcher {
	
	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(SynchronizationWatcher.class);
	
	/** The modified. */
	private AtomicBoolean modified = new AtomicBoolean(false);
	
	/**
	 * Initialize.
	 *
	 * @param folder the folder
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws InterruptedException the interrupted exception
	 */
	public void initialize(String folder) throws IOException, InterruptedException {
		if (logger.isDebugEnabled()) {logger.debug("Initializing the Registry file watcher...");}
		WatchService watchService = FileSystems.getDefault().newWatchService();
		Path path = Paths.get(folder);
		ExecutorService executor = Executors.newFixedThreadPool(1);
		registerAll(path, watchService);
		executor.submit(() -> {
			WatchKey watchKey;
			try {
				while ((watchKey = watchService.take()) != null) {
		            for (WatchEvent<?> event : watchKey.pollEvents()) {
		                String fileName = event.context().toString();
		                modified.set(true);
		                Path dir = (Path) watchKey.watchable();
		                Path fullPath = dir.resolve(fileName);
		                if (fullPath.toFile().isDirectory()) {
		                	registerAll(fullPath, watchService);
		                }
		            }
		            watchKey.reset();
		        }
			} catch (InterruptedException | IOException e) {
				logger.error(e.getMessage(), e);
			}
		});
		
		if (logger.isDebugEnabled()) {logger.debug("Done initializing the Registry file watcher.");}
	}
	
	/**
	 * Register the given directory and all its sub-directories with the WatchService.
	 *
	 * @param start the start
	 * @param watchService the watch service
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void registerAll(final Path start, WatchService watchService) throws IOException {
	    // register directory and sub-directories
	    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

	        @Override
	        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
	                throws IOException {
	            dir.register(watchService,
	            		StandardWatchEventKinds.ENTRY_CREATE,
	            		StandardWatchEventKinds.ENTRY_DELETE,
	            		StandardWatchEventKinds.ENTRY_MODIFY);
	            return FileVisitResult.CONTINUE;
	        }

	    });

	}
	
	/**
	 * Checks if is modified.
	 *
	 * @return true, if is modified
	 */
	public boolean isModified() {
		return modified.get();
	}
	
	/**
	 * Reset.
	 */
	public void reset() {
		modified.set(false);
	}

}