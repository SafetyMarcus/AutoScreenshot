# AutoScreenshot
An easy way to take localised screenshots on Android using robotium


# Using AutoScreenshot
First add autoscreenshot to your application's gradle file. Ensure that you have JCenter added as one of your repositories to either your project build.gradle or your module build.gradle.

`compile 'com.safetymarcus.autoscreenshot:autoscreenshot:1.0.1@aar'`

Set up robotium to compile into your test environment. If necessary, this can easily be done in a separate directory from your other tests by using flavours.

`androidTestCompile 'com.jayway.android.robotium:robotium-solo:+'`

It is also necessary to have commons io added to your project in order to allow for the saving and deleting of files.

`compile group: 'commons-io', name: 'commons-io', version: '+'`

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

Once the test has passed use `./adb pull "sdcard/Autoshot/"` to pull the folder of screenshots from your device. This can be changed by overwriting set up and setting up the `Solo.Config` file before creating a new `Solo` instance.

**Ensure that your manifest has the permission `<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>` as all screenshots will be written to the device sdcard.

# Customisation
By default, AutoScreenshot will take all screenshots in English. To add other languages, override `getLocales()` and return a `String[]` of the locales that you would like the screenshots to be taken in. These locales should take the form that they appear in their respective string resource folders.

```
@Override
public String[] getLocales()
{
	return new String[] {"EN", "CA", "DE", "ES", "FR", "IT"};
}
```

If you find it is necessary to dismiss dialogs that may open when your activity first starts (as I have often found) you can override `dismissDialogs()`. This is called in `setUp()` and will therefore run before each screenshot test. By default, `dismissDialogs()` is empty.

To customise the output directory of your screenshots, override `setUpOutputPath(<path>)`. This takes in a path that assumes that it is being appended to `Environment.getExternalStorageDirectory()`.

To customise the file type and other settings within the `Solo.Config`, override `setUpConfig()`. This config will haev the output path and be set as the config for the test in `setUp()`. By default, images are saved as `.png`

# Thanks
Thanks to umito (https://github.com/umito) for the original idea on using Solo to take the localised screenshots
