package be.robinj.distrohopper.desktop;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import be.robinj.distrohopper.Image;
import be.robinj.distrohopper.R;

/**
 * Created by robin on 8/21/14.
 */
public class Wallpaper extends ImageView
{
	private Context context;
	private Drawable img;
	private Drawable blurred;
	private String mode;

	private boolean liveWallpaper = false;

	public Wallpaper (Context context)
	{
		super (context);

		this.context = context;
	}

	public Wallpaper (Context context, AttributeSet attrs)
	{
		super (context, attrs);

		this.context = context;
	}

	public Wallpaper (Context context, AttributeSet attrs, int defStyle)
	{
		super (context, attrs, defStyle);

		this.context = context;
	}

	public void init ()
	{
		WallpaperManager wpman = WallpaperManager.getInstance (this.context);
		this.img = wpman.getDrawable ();

		try
		{
			//TODO// Huge memory hog! Need to get rid of this. //
			SharedPreferences prefs = this.context.getSharedPreferences ("prefs", Context.MODE_PRIVATE);
			this.mode = prefs.getString ("unitywallpaper_blur", "darken");

			if (mode.equals ("scale"))
			{
				Drawable blurred = wpman.getDrawable ();

				BitmapDrawable bmdBlurred = (BitmapDrawable) blurred;
				Bitmap bmBlurred = bmdBlurred.getBitmap ();

				float origWidth = bmBlurred.getWidth ();
				float origHeight = bmBlurred.getHeight ();

				int width = 200;
				int height = (int) (origHeight * (200F / origWidth));

				bmBlurred = Bitmap.createScaledBitmap (bmBlurred, width, height, true);
				bmBlurred = Bitmap.createScaledBitmap (bmBlurred, (int) origWidth, (int) origHeight, true);

				bmdBlurred = new BitmapDrawable (bmBlurred);
				this.blurred = bmdBlurred;
			}
		}
		catch (OutOfMemoryError ex) // I'd prefer the image not being blurred over the app crashing //
		{
			this.blurred = null;

			ex.printStackTrace ();
		}

		WallpaperInfo info = wpman.getWallpaperInfo ();
		this.liveWallpaper = (info != null);
	}

	public void set ()
	{
		if (! this.mode.equals ("no"))
		{
			if (this.liveWallpaper || this.blurred == null || this.mode.equals ("darken"))
			{
				this.setImageDrawable (null);
				this.setBackgroundColor (this.getResources ().getColor (R.color.transparent));
			}
			else
			{
				this.setImageDrawable (this.img);
				this.setBackgroundDrawable (null); // setBackgroundDrawable is deprecated, but setBackground is unspported on older versions of Android //
			}
		}
	}

	public void blur ()
	{
		if (! this.mode.equals ("no"))
		{
			if (this.liveWallpaper || this.blurred == null || this.mode.equals ("darken"))
			{
				this.setImageDrawable (null);
				this.setBackgroundColor (this.getResources ().getColor (R.color.transparentblack60));
			}
			else
			{
				this.setImageDrawable (this.blurred);
				this.setBackgroundDrawable (null); // setBackgroundDrawable is deprecated, but setBackground is unspported on older versions of Android //
			}
		}
	}

	public void unblur ()
	{
		if (! this.mode.equals ("no"))
		{
			if (this.liveWallpaper || this.blurred == null || this.mode.equals ("darken"))
			{
				this.setImageDrawable (null);
				this.setBackgroundColor (this.getResources ().getColor (R.color.transparent));
			}
			else
			{
				this.setImageDrawable (this.img);
				this.setBackgroundDrawable (null); // setBackgroundDrawable is deprecated, but setBackground is unspported on older versions of Android //
			}
		}
	}

	public int getAverageColour (int alpha)
	{
		SharedPreferences prefs = this.context.getSharedPreferences ("prefs", Context.MODE_PRIVATE);

		Image image = new Image (this.img);
		return image.getAverageColour
		(
			prefs.getBoolean ("colourcalc_advanced", true),
			prefs.getBoolean ("colourcalc_hsv", true),
			alpha
		);
	}

	public boolean isLiveWallpaper ()
	{
		return this.liveWallpaper;
	}
}
