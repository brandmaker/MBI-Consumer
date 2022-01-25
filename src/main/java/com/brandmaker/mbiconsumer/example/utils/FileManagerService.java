package com.brandmaker.mbiconsumer.example.utils;

import org.json.JSONObject;

import com.brandmaker.mbiconsumer.example.dtos.QueueEvent;

/**
 * Manages the local file copy of an asset
 *
 * <p>The files are stored in the path given by the application.yaml. There is a directory per customer and system ID:
 *
 * <pre>
 *
 *    &lt;basepath>/
 *    		&lt;customer ID>/
 *    			&lt;system id>/
 *    					event_&lt;time stamp>.json
 *    					event_&lt;time stamp>.bin
 *
 * </pre>
 * @author axel.amthor
 *
 */
public interface FileManagerService {

	/**
	 * Serialize the object to a JSON Object and store into file
	 *
	 * @param event - according event
	 * @param object - object to be serialized as json
	 *
	 */
	void storeMetadata(QueueEvent event, JSONObject object);

	/**
	 * store these binary data
	 *
	 * @param event - according event
	 * @param object - byte array of binary data
	 */
	void storeBinarydata(QueueEvent event, byte[] object);

	/**
	 * Delete all files associated to this event from the local file store
	 *
	 * @param event
	 */
	void deleteFiles(QueueEvent event);

}
