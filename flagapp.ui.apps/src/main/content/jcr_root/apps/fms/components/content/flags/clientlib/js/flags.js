$(document).ready(function() {
    $(".delete-flag").on('click', function(event) {
        let value = $(this).parent().data("flag-name");
        if (value.length > 0) {
            callFmsFlagBackend("delete", value);
        }
    });

    $(".create-flag").click(function(event) {
        let $card = $(this).closest(".new-flag-card");
        let title = $card.find("input[name='new-flag-title']").val();
        let value = $card.find("input[name='new-flag-value']").val();
        let type = $card.find("select[name='new-flag-type']").val();
        let p = '?fvalue=' + value + ((type.length > 0) ? '&ftype=' + type : '');
        if (value.length > 0 && title.length > 0) {
            callFmsFlagBackend("create", title, value, p);
        }
    });

    $(".update-flag").click(function(event) {
        let $card = $(this).closest(".flag-card");
        let title = $card.find("input[name='flag-title']").val();
        let value = $card.find("input[name='flag-value']").val();
        let type = $card.find("select[name='flag-type']").val();
        let p = '?fvalue=' + value + ((type.length > 0) ? '&ftype=' + type : '');
        if (value.length > 0 && title.length > 0) {
            callFmsFlagBackend("create", title, value, p);
        }
    });

    $(".init-update").on('click', function(event) {
        let $card = $(this).closest(".flag-card");
        $card.find("input").each((index, value) => {
            $(value).removeAttr("disabled");
        });

        $card.find("select").each((index, value) => {
            $(value).removeAttr("disabled");
        });
        $(this).addClass('d-none');
        $(this).next().removeClass('d-none');
    });

    $(".copy-flag-value").on('click', function(event) {
        let $card = $(this).closest(".flag-card");
        var copyTextarea = $card.find("input[name='flag-value']");
        copyTextarea.removeAttr('disabled');
        copyTextarea.focus();
        copyTextarea.select();
        copyTextarea.attr('disabled', "true");
        try {
            var successful = document.execCommand('copy');
            var msg = successful ? 'successful' : 'unsuccessful';
        } catch (err) {
            console.log('Oops, unable to copy');
        }
    });

    const callFmsFlagBackend = (action, flag, value, params) => {
        params = params != null ? params : "";
        let url = "/api/fms/v2/flag/" + action + "/" + selector + "/" + flag + params;
        ajaxManager.done = customDone;
        ajaxManager.always = null;
        ajaxManager.fire(url, 'GET');
    }
});