// ActionScript file

import flash.events.*;
import flash.external.*;

import mx.controls.Alert;
import mx.events.*;

var error : String;
var debugging : Boolean;

protected function debug(info : String) {
	if (debugging) {
		Alert.show(info);
	}
}

public function initApp() : void {
	error = null;
	debugging = false;
	if (!ExternalInterface.available) {
		error = "Unable to initialize external interface";
		showError();
		return;
	}
	try {
		ExternalInterface.addCallback("playIt", playIt);
		debug("playIt");
		ExternalInterface.addCallback("stopIt", stopIt);
		debug("stopIt");
		ExternalInterface.addCallback("pauseIt", pauseIt);
		debug("pauseIt")
		ExternalInterface.addCallback("getError", getError);
		debug("getError")
		ExternalInterface.addCallback("setVideoWidth", setVideoWidth);
		debug("setVideoWidth");
		ExternalInterface.addCallback("setVideoHeight", setVideoHeight);
		debug("setVideoHeight");
		ExternalInterface.addCallback("getVideoProgress", getVideoProgress);
		debug("getVideoProgress");
		ExternalInterface.addCallback("getVideoPlayLocation", getVideoPlayLocation);
		debug("getVideoPlayLocation");
		ExternalInterface.addCallback("setVideoPlayLocation", setVideoPlayLocation);
		debug("setVideoPlayLocation");
		ExternalInterface.addCallback("getVideoLength", getVideoLength);
		debug("getVideoLength");
		ExternalInterface.addCallback("loadURL", loadURL);
		debug("loadURL");
		ExternalInterface.call("flexLoadedEvent");
		debug("flexLoadedEvent");
		
	} catch (err:Error) {
		error = err.message;
		showError();
	}
	showError();
}

public function playIt() : void {
	myVid.play();
}

public function stopIt() : void {
	myVid.stop();
}

public function pauseIt() : void {
	myVid.pause();
}

public function getError() : String {
	return error;
}

public function setVideoWidth(width: int) : void {
	try {
		stack.width = width;
		myVid.width = width;
	} catch (err:Error) {
		error = err.message;
		showError();
	}
}

public function setVideoHeight(height : int) : void {
	try {
		stack.height = height;
		myVid.height = height;
	} catch (err:Error) {
		error = err.message;
		showError();
	}
}

public function getVideoPlayLocation() : Number {
	return myVid.playheadTime;
}

public function getVideoLength() : Number {
	return myVid.totalTime;
}

public function setVideoPlayLocation(time : Number) : void {
	myVid.playheadTime = time;
}

public function getVideoProgress() : Number {
	
	if (myVid.bytesTotal <= 0) {
		return 0;
	}
	return ((myVid.bytesLoaded/myVid.bytesTotal)*myVid.totalTime);
}

public function loadURL(url : String) : void {
	try {
		myVid.source=url;
		error = url;
	} catch (err:Error) {
		error = err.message;
		showError();
	}
}

protected function videoPlayHeadEvent(event : VideoEvent) : void{
	try {
		if (ExternalInterface.available) {
			ExternalInterface.call("videoPlayHeadEvent");
		}
	} catch (err:Error) {
		error = err.message;
		showError();
	}
}

protected function videoProgressEvent(event : ProgressEvent) : void {
	try {
		if (ExternalInterface.available) {
			ExternalInterface.call("videoProgressEvent");
		}
	} catch (err:Error) {
		error = err.message;
		showError();
	}
}

protected function videoReadyEvent(event : VideoEvent) :void {
	try {
		if (ExternalInterface.available) {
			ExternalInterface.call("videoReadyEvent");
		}
	} catch (err:Error) {
		error = err.message;
		showError();
	}
}

protected function videoComplete(event:VideoEvent) : void {
	try {
		if (ExternalInterface.available) {
			ExternalInterface.call("videoLoadedEvent");
		}
	} catch (err:Error) {
		error = err.message;
		showError();
	}
}

protected function showError() :void  {
	errorText.text = error;
	Alert.show(error);
	stack.selectedChild = errorView;
}