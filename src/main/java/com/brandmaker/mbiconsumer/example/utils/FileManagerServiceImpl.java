/**
 *
 */
package com.brandmaker.mbiconsumer.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.brandmaker.mbiconsumer.example.dtos.QueueEvent;
import com.brandmaker.mbiconsumer.example.dtos.WebhookTargetPayloadHttpEntity.Event;

/**
 * @see FileManagerService
 *
 * @author axel.amthor
 *
 */
public class FileManagerServiceImpl implements FileManagerService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileManagerService.class);

	/** directory where to store the local copies of the assets */
	@Value("${spring.application.system.basepath}")
	private String basepath;

	/* (non-Javadoc)
	 * @see com.brandmaker.mediapool.rest.FileManagerService#storeMetadata(com.brandmaker.mediapool.MediaPoolAsset)
	 */
	@Override
	public void storeMetadata(QueueEvent event, JSONObject object) {
		File path = getOrCreateTargetFolder(event);
		FileOutputStream outputStream =  null;

		File metadata = new File(path, "Event_" + System.currentTimeMillis() + ".json");
		try {
			outputStream = new FileOutputStream(metadata);
		    byte[] bytes = object.toString(4).getBytes();

		    outputStream.write(bytes);
		}
		catch ( Exception e ) {
			LOGGER.error("Error on writing meta data", e);
		}
		finally {

			try {
				if ( outputStream != null )
					outputStream.close();
			}
			catch ( Exception e ) {
				LOGGER.error("Error on closing streams", e);
			}

		}
	}

	/* (non-Javadoc)
	 * @see com.brandmaker.mediapool.rest.FileManagerService#storeBinarydata(com.brandmaker.mediapool.MediaPoolAsset)
	 */
	@Override
	public void storeBinarydata(QueueEvent event, byte[] object) {
		File path = getOrCreateTargetFolder(event);
		FileOutputStream outputStream = null;

		try {

			// use the actual name and suffix and create the output file
			File binary = new File(path, "Event_" + System.currentTimeMillis() + ".bin");

			// open out stream
			outputStream = new FileOutputStream(binary);

			// copy streams
		    IOUtils.write(object, outputStream);

		    LOGGER.info("Written to file " + binary.getAbsolutePath() );

		}
		catch ( Exception e ) {
			LOGGER.error("Error on writing binary data", e);
		}
		finally {

			try {
				if ( outputStream != null )
					outputStream.close();
			}
			catch ( Exception e ) {
				LOGGER.error("Error on closing streams", e);
			}

		}

	}

	/* (non-Javadoc)
	 * @see com.brandmaker.mediapool.rest.FileManagerService#deleteFiles(com.brandmaker.mediapool.webhook.MediaPoolEvent)
	 */
	@Override
	public void deleteFiles(QueueEvent event) {
		File path = getOrCreateTargetFolder(event);

		try {

			FileUtils.deleteDirectory(path);
		}
		catch (IOException e) {
			LOGGER.error("Error on removing files", e);
		}

	}


	private File getOrCreateTargetFolder(QueueEvent event) {

		String path = basepath
				+ event.getCustomerId() + "/"
				+ event.getSystemId() + "/";

		File dir = new File(path);

		dir.mkdirs();

		return dir;
	}

}
