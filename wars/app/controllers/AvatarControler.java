package controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import models.AvatarInterface;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;

import play.mvc.Controller;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.mvc.Result;
import services.api.AvatarService;

import com.google.inject.Inject;

import daos.AbstractDAO;

/**
 * 
 * @author kamil
 *
 * @param <T> - class of model which avatar is to be modified
 */
public class AvatarControler<T extends AvatarInterface> extends Controller {
	
	@Inject
	private static AvatarService avatarService;
	
	/**
	 * To be wrapped in a derivative class - to have object from http context
	 * 
	 * @param object
	 * @return
	 */
	protected static Result getAvatarTemplate(AvatarInterface object) {
		
		byte[] avatar;
		
		try {
			avatar = avatarService.getAvatar(object);
		} catch (Exception e) {
			File empty = new File("public/images/profile_default.png");
			try {
				avatar = FileUtils.readFileToByteArray(empty);
			} catch (IOException f) {
				return ok("error");
			}
		}
		
		InputStream fis = new ByteArrayInputStream(avatar);
		return ok(fis).as("image/jpeg");
	}	
	
	/**
	 * To be wrapped in a derivative class - to have object from http context
	 * 
	 * @param object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T extends AvatarInterface> Result uploadPhotoTemplate(T object, AbstractDAO<T,ObjectId> dao) {
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart picture = body.getFile("file-0");
		
		if (picture == null)
			return ok("error"); 
		 
		File file = picture.getFile();
		
		try {
			object = (T)avatarService.setAvatar(object, file);
			dao.save(object);
		} catch (IOException e) {
			return ok("error");
		}
		
		return ok("success");
	}
}
