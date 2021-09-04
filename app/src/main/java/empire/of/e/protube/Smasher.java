package empire.of.e.protube;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Smasher extends Activity {
		MyWebView web;
		WebSettings webset;
	  Activity mainThread;
		static String currentURL;
		static String dlURL;
		Intent notify;
		int selected = 3;
		AlertDialog customAlertDialog ;
		public static String titleName;
		SharedPreferences pr3fs;
		SharedPreferences.Editor prefEditor;
		Boolean shownPermWarn;
		int lastQ;
		String lastUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
				pr3fs = getSharedPreferences("Prefs", MODE_PRIVATE);
				prefEditor = pr3fs.edit();
				mainThread = this;
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				//getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
				getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
				// getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
				//getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
	    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


				createNotificationChannel();
				notify = new Intent(this, Notify.class);
				web = findViewById(R.id.frame);
				webset = web.getSettings();
				web.setOnLongClickListener(new OnLongClickListener() {
								@Override
								public boolean onLongClick(View v) {
										return true;
								}
						});
				web.setLongClickable(false);
				CookieSyncManager.createInstance(this);
				CookieSyncManager.getInstance().startSync();
				CookieManager.getInstance().setAcceptThirdPartyCookies(web, true);
				CookieManager.getInstance().setAcceptCookie(true);
				webset.setJavaScriptEnabled(true);
				webset.setJavaScriptCanOpenWindowsAutomatically(true);
				webset.setDomStorageEnabled(true);
				webset.setAppCacheEnabled(true);
				webset.setAppCacheMaxSize(4096);
				webset.setCacheMode(webset.LOAD_CACHE_ELSE_NETWORK);
				webset.setDisplayZoomControls(false);
				web.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
				webset.setUserAgentString(USER_AGENT());
				web.setDownloadListener(new DownloadListener(){
								@Override
								public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
										//titleName =	web.getTitle().replace(" - YouTube", "").replace("Vevo - ", "");

										shownPermWarn = pr3fs.getBoolean("shownPermWarn", false);
										if (!shownPermWarn) {
												AlertDialog.Builder alertD = new AlertDialog.Builder(mainThread);
												alertD.setTitle("Permissions Required");
											  alertD.setMessage("Storage Writing permissions could be required to perform this action, please enable the permission on the next screen and try again.");
												alertD.setNeutralButton("OK", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																		prefEditor.putBoolean("shownPermWarn", true);
																		prefEditor.commit();
																		mainThread.runOnUiThread(new Runnable(){
																						@Override
																						public void run() {
																								checkDownloadPermission();
																						}
																				});
																}
														});

												alertD.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
																@Override
																public void onClick(DialogInterface dialog, int which) {
																}
														});
												alertD.create().show();
										}
										else {
												checkDownloadPermission();
												lastQ = pr3fs.getInt("lastQ", 3);

												new Thread(new Runnable(){
																@Override
																public void run() {

																		// return to ui thread
																		mainThread.runOnUiThread(new Runnable(){
																						@Override
																						public void run() {
																								AlertDialog.Builder alertDiag = new AlertDialog.Builder(mainThread);
																								alertDiag.setTitle("Download " + titleName);

																								String[] listItems = new String[]{"Mp4 > High Quality", "Mp3 > High Quality", "Mp4 > Normal Quality", "Mp3 > Normal Quality"};
																								alertDiag.setSingleChoiceItems(listItems, lastQ, new DialogInterface.OnClickListener() {
																												@Override
																												public void onClick(DialogInterface dialog, final int which) {
																														toast("Fetching Download Link...");
																														prefEditor.putInt("lastQ", which);
																														prefEditor.commit();
																														// thread depth 2 
																														doDownload(which);
																												}
																										});
																								alertDiag.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
																												@Override
																												public void onClick(DialogInterface dialog, int which) {
																												}
																										});
																								alertDiag.setPositiveButton(listItems[lastQ], new DialogInterface.OnClickListener() {
																												@Override
																												public void onClick(DialogInterface dialog, int which) {
																														toast("Fetching Download Link...");
																														doDownload(lastQ);
																												}
																										});
																								customAlertDialog = alertDiag.create();
																								customAlertDialog.show();
																						}
																				});

																}
														}).start();
										}

								}
						});
				web.setWebViewClient(webClient);
				web.setWebChromeClient(webCromeClient);


				boolean shownStart = pr3fs.getBoolean("shownStart", false);
				if (shownStart == false) {
						AlertDialog.Builder alert = new AlertDialog.Builder(mainThread);
						alert.setTitle("Welcome");
						alert.setMessage("This application runs better when,\n• Maintaining a stable internet connection\n• Keeping the screen turned on ( lock screen support is buggy due to the methods used for autoplay features )\n• Waiting for pages to complete loading before pressing back\n\n\nThis app was my 5th attempt at using JavaScript reflection and injection techniques, this is simmiliar to installing an Add Free browser extension in Google Chrome, Firefox or other internet browsers.\n\nPlease do not ask me to continue this development, it has taken many hours to create the hooking methods, \"Background Playback\", \"Download\", \"Ad Free\" and \"AutoPlay\" feature support.\n\nThe app can be buggy at times with ads sometimes getting through the hardened scripts, notifications needing to be pressed twice to close them and the video frame sometimes appearing when pressing back button very quickly after opening a page, if anything goes wrong just close the app from your \"RECENT APPS\" menu or by pressing the back key.");
						alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
												doStart();
										}
								});
						alert.setNegativeButton("NEVER SHOW AGAIN", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
												prefEditor.putBoolean("shownStart", true);
												prefEditor.commit();
												doStart();
										}
								});
						alert.create().show();
				}
				else {
						doStart();
				}
    }
		public void doStart() {
				String search = "Top New Music";
				lastUrl = pr3fs.getString("lastLoad", ("https://m.youtube.com/results?search_query=" + URLEncoder.encode(search) + "&sp=EgIQAw"));
				//toast("Returning to "+lastUrl);

				if (isConnected() && lastUrl.startsWith("http")) {
						web.loadUrl(lastUrl);
				}
				else {
						web.loadUrl("file:///android_asset/index.html");
						AlertDialog.Builder alert = new AlertDialog.Builder(mainThread);
						alert.setTitle("Network Disconnected");
						alert.setMessage("Please enable your internet, the website services are unavailable without internet acces.");
						alert.setNeutralButton("RETRY", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
												onCreate(null);
										}
								});

						alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
										}
								});
						alert.create().show();
				}
		}

		public void doDownload(final int quality) {
				new Thread(new Runnable(){
								@Override
								public void run() {
										selected = quality;
										// when selected an item the dialog should be closed with the dismiss method
										customAlertDialog.dismiss();
										boolean isVideo = (selected == 0 || selected == 2);
										boolean isAudio = (selected == 1 || selected == 3);
										boolean isHQ = (selected == 0 || selected == 1);
										boolean verifyAudio = !isVideo && isAudio;
										//toast("HQ=" + isHQ + " Audio=" + verifyAudio);
										try {
												downloadYTFile(verifyAudio, isHQ);
										}
										catch (Exception e)	{
												toast(e.getMessage());
										}
								}
						}).start();
		}


		boolean videoRemoved = false;
		boolean startIgnored = false;
		private void pageLoaded(final WebView view, final String url) {
				currentURL = url;
				titleName =	web.getTitle().replace(" - YouTube", "").replace("Vevo - ", "").replace("YouTube", "").replace(" Video", "");
				titleName = titleName.replace("Video", "").replace(" video", "").replace("video", "").replace(" Vevo", "").replace(" vevo", "").replace("Vevo", "").replace("vevo", "");

				new Thread(new Runnable(){
								@Override
								public void run() {
										currentURL = url;
										final boolean searchUrl =url.contains("/results?");
										mainThread.runOnUiThread(new Runnable(){

														@Override
														public void run() {
																titleName =	web.getTitle().replace(" - YouTube", "").replace("Vevo - ", "").replace("YouTube", "").replace("Video", "");
																titleName = titleName.replace(" Video", "").replace("video", "").replace("Vevo", "").replace("vevo", "").replace(" Vevo", "").replace(" vevo", "");

														}
												});

										if (startIgnored) {


												int task = getVideoState(url);

												// on video
												if (task == 0) {
														if (videoRemoved) {
																videoRemoved = false;
																removeUnderVideoAds(view, url);
																//  toast("removing banner ads");
																String streamURL = getLinkBrute(url);
																showVideo(view, streamURL);
																//	toast("showing video");
														}
														else {
																removeUnderVideoAds(view, url);
																//			toast("removing ads");
																updatePlayingVideo(view, getLinkBrute(url));
																//			toast("updating video");
																if (!isScreenVisible) {
																		mainThread.runOnUiThread(new Runnable(){
																						@Override
																						public void run() {
																								Notify.descr = titleName;
																								startService(notify);
																						}
																				});
																}
														}
												}
												else if (task == 2) {

														if (videoRemoved == false) {
																videoRemoved = true;
																hideVideo(view);
																//toast("removing video");
														}
														if (searchUrl) {
																removeSearchAds(view, url);
													    	//toast("removing search ads");
														}
												}
										}
										else {
												startIgnored = true;
												videoRemoved = true;
										}
								}
						}).start();

		}
		private void hideVideo(final WebView view) {
				mainThread.runOnUiThread(new Runnable(){
								@Override
								public void run() {
										view.loadUrl(Commands.removeVideoFrame());
										setStatusColour(R.color.colorPrimary);
								}
						});

		}
		private void showVideo(final WebView view, final String url) {
				mainThread.runOnUiThread(new Runnable(){
								@Override
								public void run() {
										titleName =	web.getTitle().replace(" - YouTube", "").replace("Vevo - ", "").replace("YouTube", "").replace(" Video", "");
										titleName = titleName.replace("Video", "").replace(" video", "").replace("video", "").replace(" Vevo", "").replace(" vevo", "").replace("Vevo", "").replace("vevo", "");
										view.loadUrl(Commands.addVideoFrame(url));
										setStatusColour(R.color.colorAccentVideo);
								}
						});
		}

		public void setStatusColour(int color) {
				Window window =  getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.setStatusBarColor(ContextCompat.getColor(this, color));
		}

		private void removeUnderVideoAds(final WebView view, String url) {
				mainThread.runOnUiThread(new Runnable(){
								@Override
								public void run() {
										view.loadUrl(Commands.removeUnderVideoAds());
								}
						});
		}
		private void removeSearchAds(final WebView view, String url) {
				mainThread.runOnUiThread(new Runnable(){
								@Override
								public void run() {
										view.loadUrl(Commands.removeSearchAds());
								}
						});
		}
		private void updatePlayingVideo(final WebView view, final String url) {
				mainThread.runOnUiThread(new Runnable(){
								@Override
								public void run() {
										titleName =	web.getTitle().replace(" - YouTube", "").replace("Vevo - ", "").replace("YouTube", "").replace(" Video", "");
										titleName = titleName.replace("Video", "").replace(" video", "").replace("video", "").replace(" Vevo", "").replace(" vevo", "").replace("Vevo", "").replace("vevo", "");

										view.loadUrl(Commands.updatePlayingVideo(url));
								}
						});
		}
// Identify Page
		public int getVideoState(String url) {
				boolean videoUrl = url.contains("/watch?v");
				boolean searchingUrl =url.contains("#search");
				boolean playlistUrl = url.contains("&list=");

				if (videoUrl & ! searchingUrl) {

						// on video page
						return 0;
				}
				else if (videoUrl & searchingUrl) {
						// searching on video page - 
						return 1;
				}
				else {
						// remove video
						return 2;
				}
		}

		//
		//
		//
		//


		int loads = 0;
		// Webview Client
		WebViewClient webClient = new WebViewClient(){
				String url;
				@Override
				public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
						url =  request.getUrl().toString();
						//
						//
						// || ok("ads") 
						if (ok("/player/") || ok("pagead") || ok("stats") ||  ok("googlesyndication")) {
								// Kill Default Player
								//	pageLoaded(view, view.getUrl());
								return new WebResourceResponse("", "", null){};

						}
						return super.shouldInterceptRequest(view, request);
				}


				private boolean ok(String find) {
						if (url.toLowerCase().contains(find)) {
								return true;
						}
						return false;
				}


				@Override
				public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
						if (url.startsWith("http")) {
								prefEditor.putString("lastLoad", url);
								prefEditor.commit();
						}
						CookieSyncManager.getInstance().sync();
						pageLoaded(view, url);
						loads++;
						super.doUpdateVisitedHistory(view, url, isReload);
				}
		};



// Webview Chrome Client
		public  View mCustomView;
		public  WebChromeClient.CustomViewCallback mCustomViewCallback;
		protected  FrameLayout mFullscreenContainer;
		public  int mOriginalOrientation;
		public  int mOriginalSystemUiVisibility;

		WebChromeClient webCromeClient = new WebChromeClient(){

				public void onSelectionStart(WebView view) {
            // By default we cancel the selection again, thus disabling
            // text selection unless the chrome client supports it.

        }
				@Override
				public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
						return false;
				}
				@Override
				public Bitmap getDefaultVideoPoster() {
						if (mCustomView == null) {
								return null;
						}
						return super.getDefaultVideoPoster();
				}
				public void onHideCustomView() {
						((FrameLayout)mainThread.getWindow().getDecorView()).removeView(mCustomView);
						mCustomView = null;
						mainThread. getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
						mainThread.setRequestedOrientation(mOriginalOrientation);
						lockOrientationPortrait(mainThread);
						mCustomViewCallback.onCustomViewHidden();
						mCustomViewCallback = null;
				}

				public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
						if (mCustomView != null) {
								onHideCustomView();
								return;
						}
						mCustomView = paramView;
						mOriginalSystemUiVisibility = mainThread. getWindow().getDecorView().getSystemUiVisibility();
						mOriginalOrientation = mainThread. getRequestedOrientation();
						mCustomViewCallback = paramCustomViewCallback;
						lockOrientationLandscape(mainThread);
						((FrameLayout)mainThread.getWindow().getDecorView()).addView(mCustomView, new FrameLayout.LayoutParams(-1, -1));
						mainThread.getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
				}};

		public static void lockOrientationLandscape(Activity activity) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
		/** Locks the device window in portrait mode. */
		public  static void lockOrientationPortrait(Activity activity) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
		}
		/** Allows user to freely use portrait or landscape mode. */
		public  static void unlockOrientation(Activity activity) {
				activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		}
		public static  boolean isFullscreen(Activity activity) {
				return activity.getRequestedOrientation() ==  ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
		}



		@Override  
		protected void onSaveInstanceState(Bundle outState) {
			  web.saveState(outState);
				super.onSaveInstanceState(outState);  
		}
		@Override
		protected void onRestoreInstanceState(Bundle outState) {
				web.restoreState(outState);
				super.onRestoreInstanceState(outState);
		}

		// Additional Functions

		public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)
						this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();
            return (info != null && info.isConnected());
        }
        return false;
    }


		private void createNotificationChannel() {
				// Create the NotificationChannel, but only on API 26+ because
				// the NotificationChannel class is new and not in the support library
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
						CharSequence name = "Video Playback";
						String description = "Background Video Playback";
						int importance = NotificationManager.IMPORTANCE_LOW;
						NotificationChannel channel = new NotificationChannel("zoopy", name, importance);
						channel.setDescription(description);
						// Register the channel with the system; you can't change the importance
						// or other notification behaviors after this
						NotificationManager notificationManager = getSystemService(NotificationManager.class);
						notificationManager.createNotificationChannel(channel);
				}
		}

		int errors = 0;

		String api = "https://youtube-downloader-v3.herokuapp.com/";
		String apiBackup = "https://backup-plug.herokuapp.com/";

		private String getLinkBrute(String url) {
				 
				String loaded = "";
				String link =  "";
				try {
						loaded = GetPageContent(api + "video_info.php?url=" + url);
						JSONObject  jsonObj = new JSONObject(loaded);
						link = jsonObj.getJSONArray("links").getString(0);
				}
				catch (Error e) {
				}
				catch (Exception e) {
				}
				if (loaded.contains("error")) {
						errors++;
						//toast("error "+errors);
						if (errors <= 3) {
								return getLinkBrute(url);
						}
						else {
								if(api != apiBackup)
								{
								errors = 0;
								api = apiBackup;
								return getLinkBrute(url);
								}
								else
								{
										toast("SERVER ERROR : Using Direct Links");
										errors = 0;
										return getDownloadLinks(url,false,false);
								}
						}
				}
				return api + "stream.php?url=" + URLEncoder.encode(Uri.encode(link));
		}


		public void toast(final String messgae) {
				mainThread.runOnUiThread(new Runnable(){
								@Override
								public void run() {
										Toast.makeText(Smasher.this, messgae, 500).show();
								}
						});
		}


		private void checkDownloadPermission() {
				if (ActivityCompat.shouldShowRequestPermissionRationale(mainThread, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
						AlertDialog.Builder alertDial = new AlertDialog.Builder(mainThread);
						alertDial.setCancelable(false);
						alertDial.setTitle("Permission Required");
						alertDial.setMessage("Downloading requires the permission to save files on your storage.");
						alertDial.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int which) {
												ActivityCompat.requestPermissions(mainThread, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
										}
								});
						alertDial.setIcon(R.drawable.ic_launcher);
						alertDial.show();

				}
				else {
						ActivityCompat.requestPermissions(mainThread, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
				}
		}


		static String USER_AGENT() {
				Random randy = new Random();
				int r = randy.nextInt(300);
				return "Mozilla/5.0 (X11; Linux/Revision." + r + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Mobile Safari/537.36";
		}
		static List<String> cookies;
		public static String GetPageContent(String url) throws Exception, Error {
				URL obj = new URL(url);
			  HttpsURLConnection	conn = (HttpsURLConnection) obj.openConnection();
				conn.setRequestMethod("GET");
				conn.setUseCaches(false);
				conn.setConnectTimeout(8000);
				conn.setReadTimeout(30000);

				// act like a browser
				conn.setRequestProperty("User-Agent", USER_AGENT());
				conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
				if (cookies != null) {
						for (String cookie : cookies) {
								conn.addRequestProperty("Cookie", cookie.split(";", 1)[0]);
						}
				}

				int responseCode = conn.getResponseCode();
				String responseMessage = conn.getResponseMessage();

				BufferedReader in = 
            new BufferedReader(new InputStreamReader(conn.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
						response.append(inputLine + "\n");
				}
				in.close();
			 	setCookies(conn.getHeaderFields().get("Set-Cookie"));
				return response.toString();
		}

		public static List<String> getCookies() {
				return cookies;
		}

		public static void setCookies(List<String> cookies) {
				Smasher.cookies = cookies;
		}


		String dlFileName = "";
		public void downloadYTFile(Boolean isAudio, Boolean isHQ) throws Exception {
				dlFileName = titleName;
				if (isAudio) {
						dlFileName += (isHQ ?"-HQ": "") + ".mp3";
				}
				else {
						dlFileName += (isHQ ?"-HQ": "") + ".mp4";
				}
				String dlLink = getDownloadLinks(currentURL, isAudio, isHQ);
				//toast(dlLink);
				File tmp = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + dlFileName);
				if (!tmp.exists()) {
						if (dlLink != null) {
								DownloadManager.Request request =new DownloadManager.Request(Uri.parse(dlLink));
								request.setTitle(titleName);
								request.setDescription("Downloading...");
								request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
								request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, dlFileName);
								DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

								toast("Downloading : " + titleName);
								registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
								registerReceiver(onClomplete, new IntentFilter(DownloadManager.ACTION_VIEW_DOWNLOADS));
								dm.enqueue(request);
						}
				}
				else {
						toast("File Already Existed");
				}
		}

		BroadcastReceiver onClomplete = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
						Toast.makeText(getApplicationContext(), "Download Finished : " + dlFileName, Toast.LENGTH_SHORT).show();
						// update media scanner
						scanMedia();
				}
		};

		BroadcastReceiver onComplete = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
						Toast.makeText(getApplicationContext(), "Download Finished : " + dlFileName, Toast.LENGTH_SHORT).show();
						// update media scanner
						scanMedia();
				}
		};

		private void scanMedia() {
				Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
				sendBroadcast(scanFileIntent);
		}
		public String getDownloadLinks(String url, Boolean getAudio, Boolean isHQ) {
				String id = getYouTubeId(url);
				//toast(url+" :: "+id);
				String newUrl = "";
				String results = "";
				if (getAudio) {
						newUrl = "https://www.yt-download.org/@api/button/mp3/" + id;
				}
				else {
						newUrl = "https://www.yt-download.org/@api/button/videos/" + id;
				}
				try {
						//toast(newUrl);
						String html = GetPageContent(newUrl);
						//toast(html);
						Document doc = Jsoup.parse(html);
						Elements inputElements = doc.getElementsByTag("a");
						int at;
						int max = inputElements.size() - 1;
						if (getAudio) {
								if (isHQ) {
										at = 1;
								}
								else {

										at = 3;
								}
						}
						else {
								if (isHQ) {
										at = 0;
								}
								else {
										at = 1;
								}
						}
						if (at > max) {
								at = max;
						}
						Element selected = inputElements.get(at);
						results = selected.attr("href");
				}
				catch (Error e) {
						toast("couldn't fetch link : " + e.getMessage());
						results = null;
				}
				catch (Exception e) {
						toast("couldn't fetch link : " + e.getMessage());
						results = null;
				}
				return results;
		}

		private String getYouTubeId(String youTubeUrl) {
        String pattern = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";
        Pattern compiledPattern = Pattern.compile(pattern,
																									Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
		}






		boolean isScreenVisible= true;

		@Override
		protected void onPause() {
				//	Notify.title = getString(R.string.app_name);
				if (isConnected() && isScreenVisible) {
						Notify.descr = titleName;
						startService(notify);
				}
				CookieSyncManager.getInstance().stopSync();
				super.onPause();
		}

		@Override
		protected void onResume() {
				if (!isScreenVisible) {
						stopService(notify);
						isScreenVisible = true;
				}
				else {

				}
				CookieSyncManager.getInstance().startSync();

				super.onResume();
		}





		// Override  Back Button
		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
				// fix onBackPressed support
				if (keyCode == event.KEYCODE_BACK) {
						onBackPressed();
						return false;
				}
				return super.onKeyDown(keyCode, event);
		}
		@Override
		public void onBackPressed() {
				if (web.canGoBack() || isFullscreen(mainThread)) {
						if (isFullscreen(mainThread)) {
								removeCustomView();
						}
						else {
								web.goBackOrForward(-1);
								if (getVideoState(currentURL) == 3) {
										hideVideo(web);
								}
						}
				}
				else  {
						try {
								stopService(notify);
								if (!web.getUrl().startsWith("file:///android_asset/")) {
										prefEditor.putString("lastLoad", currentURL);
										prefEditor.commit();
								}
						}
						finally {
								super.onBackPressed();
						}
				}
		}
		public void removeCustomView() {
				((FrameLayout)mainThread.getWindow().getDecorView()).removeView(mCustomView);
				mCustomView = null;
			  mainThread.getWindow().getDecorView().setSystemUiVisibility(mOriginalSystemUiVisibility);
				mainThread.setRequestedOrientation(mOriginalOrientation);
				lockOrientationPortrait(mainThread);
				mCustomViewCallback.onCustomViewHidden();
				mCustomViewCallback = null;
		}
}

// Override Window Background
class MyWebView extends WebView {
		public MyWebView(Context context) {
				super(context);
		}
		public MyWebView(Context context, AttributeSet attrs) {
				super(context, attrs);
		}
		public MyWebView(Context context, AttributeSet attrs, int defStyleAttr) {
				super(context, attrs, defStyleAttr);
		}
		@Override
		protected void onWindowVisibilityChanged(int visibility) {
				boolean useBackground = true;
				if (useBackground) {
						if (visibility != View.GONE && visibility != View.INVISIBLE && visibility != View.SCREEN_STATE_ON)
								super.onWindowVisibilityChanged(visibility);
				}
				else 
						super.onWindowVisibilityChanged(visibility);
		}
}
