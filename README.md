# ProTube, YouTube JS Hooks
ProTube was my attempt at making a JavaScript browser extension for Android devices.
![Screenshot](Screenshot_20220924_123637.jpg)
1, The Plan    
> The project uses a WebView with JavaScript reflection - for accessing websites using cookie session storage, it will retrieve the data from a URL.      
     
2, Intercepted Ads       
> The data returned from m.youtube, is intercepted via adding additional JavaScript to each page - when you load YouTube, a script will be generated based on the page and then injected to the page to remove any advertising.    
   
3, Hooking        
> The main concept was designed to implement JavaScript hooking and reflection similiar to "JavaScript Browser Extensions" on Desktop computers.
       
4, Removing Advertisement,        
> Servers are used to load a direct link to the MP4 of the current page, when the video is is identified it will completely remove the office YouTube player and then inject a custom HTML5 player into the page instead - the custom HTML5 player is ver basic and only needs to load a video to replace the official one that includes advertisement.     
- 3x servers added for example         
     
5, Enabling AutoPlay
> AutoPlay was achieved by using a JavaScript timeout function, the video duration is extracted and then used to generate a script for a redirection after the video ends.  - it is off by about 3 seconds.    
> Redirection is achieved by loading the "Up Next" list into an array of id's ( Up Next Array )    
      
6, Enabling Background Playback on Android 10+    
> Background playback is disabled as Android 10 has changed the possibility to run an application in the background - instead one must run a service and show a notification at all times - the notification must be updated to keep the service running otherwise it'll be killed by the memory cleaner.       
> To get around Android 10+ restrictions, it will run a constant dummy service in the background that just updates the notification with "Playing Now".   
  
7, Allowing Downloading like Premium,    
> Downloads are achieved very easily - the link for the MP4 stream is just sent to the user to initiate a download via the system download manager.    
   
8, I hate multiple activities - so enjoy the hell storm 1 pagers.


### History   
Android devices are slowly being migrated to include Google Chrome as the default web browser for Android device's, However the Android version of Google Chrome does not allow installing JavaScript Extensions like the desktop counterpart, the only way around this is to use a custom web browser that has javascript interfaces designed specifically for injection of JavaScript, Resource Loading overrides and Page load control.    
    
### How Does it Work ?   
The main concept of this implementation is to mimic a famous Google Chrome extension on Desktop PC's called "AD Block for YouTube".     
   
The ProTube Project uses JavaScript injection on every page load to not only replace the video frame with a direct link from google service's ( bypassing in-video advertising ) via a third-party GitHub project that can be found by searching "YouTube Download for PHP", it also identifies and removes advertising on each page that i could identify in 3 days of testing - it does this removal of advertisng every 100 milliseconds.        
    
Purely for Testing purposes, i also tried to make it support background playback - the background playback is therefore buggy and needs some work.      
   
    
## Features     
> Ad' Free browsing of YouTube with similar features to Premium.
     
- Page and In-Video advertising removal       
- Background Playback"ish"    
- 3 Server cycling on errors       
- Off Screen Playback"ish"     
- Next Video Identification    
- AutoPlay Implementations for both Playlists and the What's Next list     
   
   
The guts,    
```
package empire.of.e.protube;
public class Commands {
		static String cmd = "JavaScript:";
		public static String addVideoFrame(String url) {

			  return cmd
						+ "setTimeout(function(){ "
						// add frame
						+ "var divs = document.getElementsByTagName(\"div\");"
						//+ "alert(\"Added Frame\");"
						+ "divs[0].innerHTML ='<video id=\"playerContent\"  style=\"background:black;\" width=\"100%\" height=\"260\"  controls>"
						+ "<source src=\"" + url + "\" type=\"video/mp4\">"
						+ "Unsupported</video>"
						+ "<div id=\"message\" style=\"background:black;color:white;width:100%;padding-bottom:2px;padding-right:2px;text-align:center;padding-top:0px;margin-top:-3px;font-size=4px;position: absolute;white-space: nowrap ;filter:alpha(opacity=50); opacity:0.5;\">ADS REMOVED</div>';"
						+ "var video = document.getElementById(\"playerContent\");"
						+ "var msg = document.getElementById(\"message\");"
						+ "video.load();"
						+ "video.addEventListener('loadeddata', function() {"	
						+ "msg.style='padding:0px;';"
						+ "msg.innerHTML='';"
						+ "video.play();"
						+ "}, false);"
						+ "video.addEventListener('ended',function() {"
						//+ "alert(\"listening for ended\");"
						+ "var testLink = \"" + Smasher.currentURL + "\";"
						//+ "alert(testLink);"
						//+ "alert(\"next on playlist\");"
						+ "if(testLink.includes(\"&list=\")){"
						+ "var buttons = document.getElementsByTagName(\"button\");"
						+ "if(buttons[6].outerHTML.includes(\"Next\")){"
						+ "buttons[6].click();"
						+ "}else{"
						+ "buttons[7].click();"
						+	"}"
						+ "}else{"
			  		//	+ "alert(\"next on normal\");"
						//	+ "alert(\"next on normal\");"
						+ "var lists = document.getElementsByTagName(\"ytm-video-with-context-renderer\");"
						+ "var nextVideo = lists[0].getElementsByTagName(\"a\")[0];"
			  	  + "nextVideo.click();"
						// end test link else
						+ "}"
						//end function
						+ "}, false);"
						+  "}, 0);";
		}
		public static String updatePlayingVideo(String url) {
				return cmd
					+ "setTimeout(function(){ "
						+ "var video = document.getElementById(\"playerContent\");"
						+ "var msg = document.getElementById(\"message\");"
						+ "video.src='" + url + "';"
						+ "video.load();"
						+ "video.play();"
				    +  "}, 200);";
		}

		public static String removeSearchAds() {
				return cmd
						+ "setTimeout(function(){ "
						//	+ "alert(\"started remove\");"
						+ "var checkExist = setInterval(function() {"
						+ "var items = document.getElementsByTagName(\"ytm-promoted-sparkles-text-search-renderer\");"
						+ "var text = \"\";"
						+ "var done = false;"
				    + "for (let i = 0; i < items.length; i++) {"
						+ "	if(items[i].innerHTML.includes(\"VISIT SITE\")){"
						+ "text = i +\"\\n\";"
						+ "items[i].innerHTML = '';"
						+ "done = true;"
						+ "}"
						+ "	}"  
						// + "alert(text);"
						+ "if(done){"
						+ "clearInterval(checkExist);"
						+ "}"
						+ "}, 100);"
						+  "}, 100);";
		}

		public static String removeUnderVideoBannerAds() {
				return cmd
				    + "setTimeout(function(){ "
						+ "var checkExist = setInterval(function() {"
						+ "var items = document.getElementsByClassName(\"compact-media-item\");"
						+ "if (items[0].parentNode.innerHTML.includes(\"sparkles\")) {"
						+ "items[0].parentNode.parentNode.parentNode.parentNode.innerHTML = '<div><p>BOOM</p></div>';"
				  //  + "clearInterval(checkExist);"
						+ "}}, 50);"
						+  "}, 100);";
		}

		public static String removeUnderVideoAds() {
				return cmd
						+ "setTimeout(function(){ "
						+ "var checkExist = setInterval(function() {"
						+ "var items = document.getElementsByTagName(\"ytm-promoted-sparkles-web-renderer\");"
						+ "var text = \"\";"
						+ "var done = false;"
				    + "for (let i = 0; i < items.length; i++) {"
						+ "text = i +\"\\n\";"
						+ "items[i].parentNode.innerHTML = '';"
						+ "done = true;"
		  			+ "	}"  
						+ "if(done){"
					//	+ "clearInterval(checkExist);"
						+ "}"
						+ "}, 50);"
						+  "}, 100);";
		}

		public static String removeVideoFrame() {
				return cmd
						+ "setTimeout(function(){ "
						+ "var video = document.getElementById(\"playerContent\");"
						+ "video.src='';"
						+ "video.load();"
						+ "video.play();"
						+ "video.innerHTML='';"
						+ "video.parentElement.innerHTML='';"
				    +  "}, 0);";
		}
}

```  
