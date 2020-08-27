$(document).ready(function() {
    $("#create-project").on('click', function(event) {
        let value = $("input[name='create-project']").val();
        if (value.length > 0) {
            callFmsBackend("create", value);
        }
    });

    $(".delete-project").on('click', function(event) {
        let value = $(this).data("project-name");
        if (value.length > 0) {
            callFmsBackend("delete", value);
        }
    });

    $("input[name='create-project']").keypress(function(event) {
        if (event.keyCode === 13) {
            $("#create-project").trigger('click');
        }
    });

    const callFmsBackend = (action, value) => {
        let createProject = "/api/fms/v2/project/" + action + "/" + value;
        ajaxManager.done = customDone;
        ajaxManager.always = null;
        ajaxManager.fire(createProject, 'GET');
    }
});