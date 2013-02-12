package services.api;

import java.io.File;
import java.io.IOException;

import models.AvatarInterface;

/**
 * 
 * @author kamil
 *
 */
public interface AvatarService extends Service {
	
	/**
	 * Sets file as the avatar of the player
	 * It does not save the object using any DAO
	 * 
	 * @param player
	 * @param file
	 * @throws when there is a problem with reading file
	 */
	public AvatarInterface setAvatar(AvatarInterface object, File file) throws IOException;

	/**
	 * Gets the player avatar
	 * @param p
	 * @return
	 */
	byte[] getAvatar(AvatarInterface object) throws Exception;

}
