package marcus.com.autoscreenshot;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import com.robotium.solo.Solo;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

/**
 * @author Marcus Hooper
 */
public abstract class ScreenshotTest<T extends Activity> extends ActivityInstrumentationTestCase2<T>
{
	public static final long ONE_DAY = 1000l * 60l * 60l * 24l;

	public Solo solo;
	public String baseOutputPath;
	public Solo.Config config;
	public int screenshotIndex;
	public String locale;

	public ScreenshotTest(Class activityClass)
	{
		super(activityClass);
	}

	@Override
	protected void setUp() throws Exception
	{
		baseOutputPath = Environment.getExternalStorageDirectory() + "/Autoshot/LocalisedScreenshot/";

		// PNG screenshots to correct outdir
		config = new Solo.Config();
		config.screenshotFileType = Solo.Config.ScreenshotFileType.PNG;
		config.screenshotSavePath = baseOutputPath;

		solo = new Solo(getInstrumentation(), config);
		getActivity();
		dismissDialogs();

		super.setUp();
	}

	public void changeActivityLocale(final Activity a, String locale) throws IOException
	{
		Resources res = a.getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		if(locale.contains("-"))
		{
			String[] split = locale.split("-");
			conf.locale = new Locale(split[0], split[1]);
		}
		else
			conf.locale = new Locale(locale);

		res.updateConfiguration(conf, dm);
		a.getResources().updateConfiguration(conf, dm);

		this.locale = locale;
	}

	/**
	 * Gets a random day within the time period specified
	 *
	 * @param numberOfDays integer specifying the max day
	 *                     in the future
	 *
	 * @return a GregorianCalendar object set to the day
	 */
	public GregorianCalendar getRandomDayInTheFuture(int numberOfDays)
	{
		Random random = new Random();
		GregorianCalendar time = new GregorianCalendar();
		time.setTimeInMillis(System.currentTimeMillis() + (random.nextInt(numberOfDays) * ONE_DAY));
		return time;
	}

	/**
	 * Gets a random day within the time period specified
	 *
	 * @param numberOfDays integer specifying the max day
	 *                     in the past
	 *
	 * @return a GregorianCalendar object set to the day
	 */
	public GregorianCalendar getRandomDayInThePast(int numberOfDays)
	{
		Random random = new Random();
		GregorianCalendar time = new GregorianCalendar();
		time.setTimeInMillis(System.currentTimeMillis() - (ONE_DAY * random.nextInt(numberOfDays)));
		return time;
	}

	/**
	 * Takes the actual screenshot
	 *
	 * @param tag The tag for saving the screenshot
	 * @throws Throwable
	 */
	public void takeLocalizedScreenshots(String tag) throws Throwable
	{
		String[] locales = getLocales();
		for(String locale : locales)
		{
			//Restart main activity with the proper locale and update the screenshot path
			resetScreenshotIndex();
			changeActivityLocale(getActivity(), locale.toUpperCase());
			restartActivity();
			updateCurrentScreenshotPath(tag);

			//Take the screenshots
			takeScreenshot(tag);
		}
	}

	private void restartActivity()
	{
		Intent intent = getActivityIntent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Start new task, and auto finish all previous activities
		getActivity().startActivity(intent);
		dismissDialogs();

		solo.sleep(5000);
	}

	protected void takeScreenshot(String name) throws Exception
	{
		solo.takeScreenshot(locale + "_" + ++screenshotIndex + "_" + name);
		solo.sleep(100);
	}

	public void resetScreenshotIndex()
	{
		this.screenshotIndex = 0;
	}

	/**
	 * Sets the save path for the screenshot to be
	 * baseOutputPath/deviceModel/tag/locale/
	 *
	 * @param tag the Tag for the current screenshot e.g. MainScreenshot
	 * @throws IOException
	 */
	private void updateCurrentScreenshotPath(String tag) throws IOException
	{
		config.screenshotSavePath = baseOutputPath + Build.MODEL + '/' + tag + '/' + locale + '/';

		FileUtils.deleteDirectory(new File(config.screenshotSavePath));
		new File(config.screenshotSavePath).mkdirs();
	}

	@Override
	protected void tearDown() throws Exception
	{
		solo.finishOpenedActivities();

		super.tearDown();
	}

	/**
	 * Implement this to return a list of locale codes.
	 * e.g. ES relates to values-es. This includes EN
	 * (English) by default
	 *
	 * @return a list of different locales to use.
	 */
	public String[] getLocales()
	{
		String[] locales = new String[1];
		locales[0] = "EN";
		return locales;
	}

	/**
	 * Implement this if you need to have dialogs dismissed
	 * when starting your activity. It is called after the
	 * activity is started in {@link #restartActivity()}
	 */
	public void dismissDialogs()
	{
		//Do nothing
	}

	/**
	 * This should be implemented by each activity that the screenshots
	 * are being taken in. When the screenshots are taken, the activity
	 * stack is cleared and the activity is started again. This allows
	 * an intent with extra information to be passed in each time.
	 *
	 * @return The intent to be started when called by {@link #restartActivity()}
	 */
	public abstract Intent getActivityIntent();
}
