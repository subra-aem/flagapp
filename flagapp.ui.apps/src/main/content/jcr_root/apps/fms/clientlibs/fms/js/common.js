let path = window.location.pathname;
let possibleSelector = path.replace(".html","").split(".")[1]
let selector = (possibleSelector != undefined & possibleSelector.length > 0) ? possibleSelector : "";

let success = `<div class="alert alert-success flag-ajax-alert">SUCCESS: Operation Succesful</div>`;
let failure = `<div class="alert alert-danger flag-ajax-alert">FAILURE: Operation Failrd</div>`;
let reqStatus = localStorage.getItem("fms-req-status");
localStorage.removeItem("fms-req-status");
if(reqStatus != null && reqStatus == "SUCCESS")
    $(success).insertBefore("div.root");
else if(reqStatus != null && reqStatus == "FAILURE")
    $(failure).insertBefore("div.root");

setTimeout(function(){
  $(".flag-ajax-alert").remove();
}, 3000);

const customDone = (data, textStatus, jqXHR) => {
	localStorage.setItem("fms-req-status", data.STATUS);
	location.reload();
}

