function enableDesign() {
	$('.sbo-navbar-item').parent().contents().unwrap();
}

var resizeIframe = function(addHeight) { 
	var height = document.getElementById("page").scrollHeight + addHeight;
    parent.postMessage(height, "*");
};

function enableIframeResize() {
	 var height = document.getElementById("page").scrollHeight - 200;
	 parent.postMessage(height, "*");
	 setTimeout(resizeIframe(300), 200);
}

$(function() {
$('.sbo-navbar-item').parent().contents().unwrap();
enableIframeResize();
});