function play() {
	var vid = getVid();
	if (!vid) {
		return "no video"
	}
	vid.playIt();
	return null;
}


function stop() {
	var vid = getVid();
	if (!vid) {
		return "no video"
	}
	vid.stopIt();
	return null;
}

function pause() {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	vid.pauseIt();
	return null;
}

function getError() {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	return vid.getError();
}

function load(url) {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	vid.loadURL(url);
	return null;
}

function setVideoWidth(width) {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	//vid.width = width;
	vid.setVideoWidth(width);
	return null;
}

function setVideoHeight(height) {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	//vid.height = height;
	vid.setVideoHeight(height);
	return null;
}


function flexLoadedEvent() {
	document.title = "VideoEvent:FlexLoaded";
	window.status = "VideoEvent:FlexLoaded";
}

function videoLoadedEvent() {
	document.title = "VideoEvent:VideoLoaded";
	window.status = "VideoEvent:VideoLoaded";
}

function videoReadyEvent() {
	document.title = "VideoEvent:VideoReady";
	window.status = "VideoEvent:VideoReady";
}

function videoProgressEvent() {
	document.title = "VideoEvent:VideoLoadProgress";
	window.status = "VideoEvent:VideoLoadProgress";
}

function videoPlayHeadEvent() {
	document.title = "VideoEvent:VideoPlayHead";
	window.status = "VideoEvent:VideoPlayHead";
}

function getVideoProgress () {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	return vid.getVideoProgress();
}

function getVideoPlayLocation() {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	return vid.getVideoPlayLocation();
}

function setVideoPlayLocation(location) {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	if (location >= 0 && location <= getVideoLength()) {
		vid.setVideoPlayLocation(location);
	}
}

function getVideoLength() {
	var vid = getVid();
	if (!vid) {
		return "no video";
	}
	return vid.getVideoLength();
}

function getVid() {
	return returnById("main");
}

function returnById( id ) {
if (navigator.appName.indexOf ("Microsoft") !=-1) {
	var obj = window[id];
	if (!obj) {
		obj = document[id];
	}
	return obj;
} else {
	var obj = document[id];
	if (obj == null) {
		obj = document.getElementById(id);
	}
    return obj;
}
//return document.getElementById(id);
}