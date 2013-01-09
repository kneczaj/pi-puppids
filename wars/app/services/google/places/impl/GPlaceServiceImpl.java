package services.google.places.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.Location;
import models.PlaceType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import play.Logger;
import services.google.places.api.GPlace;
import services.google.places.api.GPlaceService;
import services.google.places.api.GPlaceServiceException;
import services.google.places.api.RequestURL;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Wrapper for easy access from Java to the RESTful Google Places API
 * 
 * @author markus
 */
public class GPlaceServiceImpl implements GPlaceService {

	private static final String RADAR_SEARCH_PREFIX = "radarsearch";
	private static final String SEARCH_PREFIX = "search";
	private static final String DETAILS_PREFIX = "details";

	@Override
	public GPlace details(String reference) throws GPlaceServiceException {
		RequestURL request = new RequestURL();
		request.searchType = DETAILS_PREFIX;
		request.reference = encode(reference);

		try {
			String jsonResults = getResponseContent(request);
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONObject result = jsonObj.getJSONObject("result");

			GPlace place = getPlaceFromJson(result);
			return place;
		} catch (JSONException e) {
			throw new GPlaceServiceException("Could not parse JSON object", e);
		}
	}

	@Override
	public Map<Location, String> radarSearchByPlaceTypes(
			List<PlaceType> placeTypes, Location location, int radius)
			throws GPlaceServiceException {
		Map<Location, String> results = Maps.newHashMap();

		RequestURL request = new RequestURL();
		request.searchType = RADAR_SEARCH_PREFIX;
		request.latitude = location.getLatitude();
		request.longitude = location.getLongitude();
		request.radius = radius;
		request.placeTypes = placeTypes;

		try {
			String jsonResults = getResponseContent(request);
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray array = jsonObj.getJSONArray("results");

			for (int i = 0; i < array.length(); i++) {
				JSONObject current = array.getJSONObject(i);
				JSONObject geometry = current.getJSONObject("geometry");

				String reference = current.getString("reference");
				Location loc = new Location();
				loc.setLatitude(geometry.getDouble("lat"));
				loc.setLongitude(geometry.getDouble("lng"));

				results.put(loc, reference);
			}

			return results;
		} catch (JSONException e) {
			throw new GPlaceServiceException("Could not parse JSON object", e);
		}
	}

	@Override
	public ArrayList<GPlace> search(String keyword, Location location,
			int radius) throws GPlaceServiceException {
		ArrayList<GPlace> resultList = Lists.newArrayList();

		RequestURL request = new RequestURL();
		request.searchType = SEARCH_PREFIX;
		request.keywords = Lists.newArrayList(encode(keyword));
		request.longitude = location.getLongitude();
		request.latitude = location.getLatitude();
		request.radius = radius;

		String jsonResults;
		try {
			jsonResults = getResponseContent(request);
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray results = jsonObj.getJSONArray("results");

			for (int i = 0; i < results.length(); i++) {
				GPlace place = getPlaceFromJson(results.getJSONObject(i));
				resultList.add(place);
			}
		} catch (JSONException e) {
			throw new GPlaceServiceException("Could not parse JSON object", e);
		}

		return resultList;
	}

	/**
	 * Converts a JSON-Object to a GPlace-Object
	 * 
	 * @param jsonObj
	 * @return a GPlace
	 * @throws JSONException
	 */
	public static GPlace getPlaceFromJson(JSONObject jsonObj)
			throws JSONException {
		GPlace place = new GPlace();

		place.setName(jsonObj.getString("name"));
		place.setIcon(jsonObj.getString("icon"));
		place.setUuid(jsonObj.getString("id"));

		JSONObject j = jsonObj.getJSONObject("geometry").getJSONObject(
				"location");
		place.setLatitude(j.getDouble("lat"));
		place.setLongitude(j.getDouble("lng"));

		if (jsonObj.has("formatted_address")) {
			place.setAddress(jsonObj.getString("formatted_address"));
		}

		if (jsonObj.has("rating")) {
			place.setRating((float) jsonObj.getDouble("rating"));
		}

		place.setReference(jsonObj.getString("reference"));

		if (jsonObj.has("types")) {
			JSONArray types = jsonObj.getJSONArray("types");
			List<String> typeList = Lists.newArrayList();
			for (int i = 0; i < types.length(); i++) {
				typeList.add(types.get(i).toString());
			}
			place.setTypes(typeList);
		}

		if (jsonObj.has("vicinity")) {
			place.setVicinity(jsonObj.getString("vicinity"));
		}

		return place;
	}

	/**
	 * Sends the request to the Google Places API and returns the result that is
	 * coming back as String.
	 * 
	 * @param request
	 * @return the content of the Google Places API response as String
	 * @throws GPlaceServiceException
	 *             is thrown if the URL was malformed or a problem occured
	 *             during data transmission
	 */
	private static String getResponseContent(RequestURL request)
			throws GPlaceServiceException {
		StringBuilder response = new StringBuilder();
		HttpURLConnection conn = null;

		try {
			URL url = new URL(request.toString());
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				response.append(buff, 0, read);
			}
			return response.toString();
		} catch (MalformedURLException e) {
			throw new GPlaceServiceException(
					"Could not request PlacesAPI because the URL is malformed",
					e);
		} catch (IOException e) {
			throw new GPlaceServiceException(
					"Could not request PlacesAPI because there happened an error during data transmission");
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	private static String encode(String str) {
		try {
			return URLEncoder.encode(str, "utf8");
		} catch (UnsupportedEncodingException e) {
			Logger.warn("Unsupported Encoding");
			return str;
		}
	}

}