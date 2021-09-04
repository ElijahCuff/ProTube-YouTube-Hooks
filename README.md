# ProTube
ProTube was my attempt at making a JavaScript browser extension for Android devices
   
     
> The main concept was designed to implement JavaScript hooking and reflection similiar to "JavaScript Browser Extensions" on Desktop computers.
   
### History   
Android devices are slowly being migrated to include Google Chrome as the default web browser for Android device's, However the Android version of Google Chrome does not allow installing JavaScript Extensions like the desktop counterpart, the only way around this is to use a custom web browser that has javascript interfaces designed specifically for injection of JavaScript, Resource Loading overrides and Page load control.    
    
### How Does it Work ?   
The main concept of this implementation is to mimic a famous Google Chrome extension on Desktop PC's called "AD Block for YouTube", it uses JavaScript injection on every page load to not only replace the video frame with a direct link from google service's ( bypassing in-video advertising ) via a third-party GitHub project that can be found by searching "YouTube Download for PHP", it also identifies and removes advertising on each page that i could identify in 3 days of testing - it does this removal of advertisng every 100 milliseconds.        
    
Purely for Testing purposes, i also tried to make it support background playback - the background playback is therefore buggy and needs some work.      
   
    
## Features     
Ad' Free browsing of m.youtube.com      
- Page and In-Video advertising removal       
- Background Playback"ish"    
- 3 Server cycling on errors       
- Off Screen Playback"ish"     
- Next Video Identification    
- AutoPlay Implementations for both Playlists and the What's Next list     
