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
