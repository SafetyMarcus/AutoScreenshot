package marcus.com.autoscreenshot;

import android.content.Intent;

/**
 * @author Marcus Hooper
 */
public class MainActivityTest extends ScreenshotTest<MainActivity>
{
	public MainActivityTest()
	{
		super(MainActivity.class);
	}

	@Override
	public void dismissDialogs()
	{
	}

	@Override
	public Intent getActivityIntent()
	{
		return new Intent(getActivity(), getActivity().getClass());
	}

	public void testMainActivity() throws Throwable
	{
		takeLocalizedScreenshots("MainActivity");
	}
}
