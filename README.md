# AutoScreenshot
An easy way to take localised screenshots on Android using robotium

# Set Up
First add autoscreenshot to your application's gradle file.

`compile 'com.safetymarcus.autoscreenshot:1.0'` (Hasn't been released to JCenter/Maven yet)

Set up robotium to compile into your test environment. This can easily be done in a separate directory from your other tests by using flavours.

`androidTestCompile 'com.jayway.android.robotium:robotium-solo:5.3.1'`

Next add a test for the activity that you want to take screenshots in. This test should extend `ScreenshotTest<YourActivity>`. You will be required to override `getActivityIntent()`. This is a convenience method that allows you to pass in information needed when setting up your activity when changing between the different locales. 

```
@Override
public Intent getActivityIntent()
{
	return new Intent(getActivity(), getActivity().getClass());
}
```

Now just set up a normal test and make sure that in it you call `takeLocalisedScreenshots(<Tag>)`. This will cause the actual screenshots to be taken. The tag that you pass in is the subfolder that your screenshots will be saved to. Now just run your tests on a device and watch as it navigates and saves images.

```
public void testTakeMainActivityScreenshost() throws Throwable
{
	takeLocalizedScreenshots("MainActivity");
}
```

Once the test has passed use `./adb pull "sdcard/Autoshot/"` to pull the folder of screenshots from your device. This can be changed by overwriting set up and setting up the `Solo` `Config` file before creating a new `Solo` instance.

**Ensure that your manifest has the permission `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>` as all screenshots will be written to the device sdcard.
