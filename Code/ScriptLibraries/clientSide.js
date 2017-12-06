function enableDesign() {
	$('.sbo-navbar-item').parent().contents().unwrap();
}

var resizeIframe = function(addHeight) { 
	var height = document.getElementById("view:_id1:page").scrollHeight + addHeight;
    parent.postMessage(height, "*");
};

function enableIframeResize() {
	 var height = document.getElementById("view:_id1:page").scrollHeight - 200;
	 parent.postMessage(height, "*");
	 setTimeout(resizeIframe(300), 200);
}

$(function() {
	$('.sbo-navbar-item').parent().contents().unwrap();
		
	enableIframeResize();
	
	$('.navbar-toggle').click(function(e) {
		 $('.applayout-column-left').toggleClass('hidden-xs');
		 console.log('removed');
	});
});
