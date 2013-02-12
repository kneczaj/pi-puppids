package services.impl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import models.AvatarInterface;
import models.Photo;
import models.Player;

import org.apache.commons.io.FileUtils;

import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;
import services.api.AuthenticationService;
import services.api.AvatarService;

public class AvatarServiceImpl implements AvatarService {

	@Override
	public AvatarInterface setAvatar(AvatarInterface player, File file) throws IOException {
		Photo avatar = new Photo();

		avatar = loadPhotoFromFile(avatar, file);
		
		player.setAvatar(avatar);
		
		return player;
	}
	
	@Override
	public byte[] getAvatar(AvatarInterface p) throws Exception {
		if (p.getAvatar() == null)
			throw new Exception("avatar not set");
		byte [] avatar = p.getAvatar().getThumbnail();
		if (avatar == null)
			throw new Exception("avatar not set");
		return avatar;
	}
	
	/**
	 * Loads thumbnail photo from given file and stores it in photo
	 * @param photo
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Photo loadPhotoFromFile(Photo photo, File file) throws IOException {
		BufferedImage img = ImageIO.read(file);
		int thumbWidth = 200;
		int thumbHeight = 200;
		
		if (img.getWidth() > img.getHeight()) {
			thumbWidth = 200;
			thumbHeight = (int)((200d/img.getWidth())*img.getHeight());
		} else {
			thumbHeight = 200;
			thumbWidth = (int)((200d/img.getHeight())*img.getWidth());
		}			
		
		BufferedImage thumb = new BufferedImage(thumbWidth, thumbHeight,
				BufferedImage.TYPE_INT_RGB);

		// BufferedImage has a Graphics2D
		Graphics2D g2d = (Graphics2D) thumb.getGraphics();
		g2d.drawImage(img, 0, 0, thumb.getWidth() - 1,
				thumb.getHeight() - 1, 0, 0, img.getWidth() - 1,
				img.getHeight() - 1, null);
		g2d.dispose();

		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(thumb, "jpg", os);

		photo.setThumbnail(os.toByteArray());
		return photo;
	}

}
