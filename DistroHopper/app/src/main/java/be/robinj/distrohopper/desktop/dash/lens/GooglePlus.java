package be.robinj.distrohopper.desktop.dash.lens;

import android.content.Context;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import be.robinj.distrohopper.R;

/**
 * Created by robin on 4/11/14.
 */
public class GooglePlus extends Lens
{
	private final String API = "https://www.googleapis.com/plus/v1/activities?key=AIzaSyA1LFfEZYf7UapVn06Xnh69e13xbgr4-zg&query={:QUERY:}";

	public GooglePlus (Context context)
	{
		super (context);

		this.icon = context.getResources ().getDrawable (R.drawable.dash_search_lens_googleplus);
	}

	public String getName ()
	{
		return "Google+";
	}

	public String getDescription ()
	{
		return "Public Google+ activity search results";
	}

	public List<LensSearchResult> search (String str) throws IOException, JSONException
	{
		String apiResults = this.downloadStr (this.API.replace ("{:QUERY:}", URLEncoder.encode (str, "UTF-8")));

		JSONObject json = new JSONObject (apiResults);
		JSONArray items = json.getJSONArray ("items");
		List<LensSearchResult> results = new ArrayList<LensSearchResult> ();

		for (int i = 0; i < items.length (); i++)
		{
			JSONObject item = items.getJSONObject (i);

			if (item.has ("title") && item.has ("url") && (! item.getString ("title").trim ().equals ("")))
			{
				Drawable icon = this.icon;

				if (item.has ("actor"))
				{
					JSONObject actor = item.getJSONObject ("actor");

					if (actor.has ("image"))
					{
						JSONObject image = actor.getJSONObject ("image");

						if (image != null)
						{
							String imageUrl = image.getString ("url");

							if (imageUrl != null)
								icon = this.downloadImage (imageUrl.replace ("?sz=50", "?sz=64"));
						}
					}
				}

				LensSearchResult result = new LensSearchResult (this.context, item.getString ("title"), item.getString ("url"), icon);

				results.add (result);
			}
		}

		return results;
	}
}
