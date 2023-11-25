function search() {
    doAction('search', { word: $('#search_key_word').val() });
}

function api() {
    doAction('api', { url: $('#diy_api_url').val() });
}

function push() {
    doAction('push', { url: $('#push_url').val() });
}

function store() {
    doAction('store', { url: $('#diy_store_url').val() });
}

function live() {
    doAction('live', { url: $('#diy_live_url').val() });
}

function epg() {
    doAction('epg', { url: $('#diy_epg_url').val() });
}

function proxy() {
    doAction('proxy', { url: $('#diy_proxy_url').val() });
}

function doAction(action, kv) {
    kv['do'] = action;
    // alert(JSON.stringify(kv));
    $.post('/action', kv, function (data) {
        console.log(data);
        // alert(data);
    });
    return false;
}

function tpl_top(path) {
    return `<a class="weui-cell  weui-cell_access" href="javascript:void(0)" onclick="listFile('` + path + `')">
    <div class="weui-cell__hd"><img src="`+ ic_dir + `" alt="" style="width: 32px; margin-right: 16px; display: block;"></div>
    <span class="weui-cell__bd">
        <span>..</span>
    </span>
    <span class="weui-cell__ft">
    </span>
    </a>`;
}

function tpl_dir(name, time, path) {
    return `<a class="weui-cell  weui-cell_access" href="#" onclick="listFile('` + path + `')">
    <div class="weui-cell__hd"><img src="`+ ic_dir + `" alt="" style="width: 32px; margin-right: 16px; display: block;"></div>
    <span class="weui-cell__bd">
    <span>`+ name + `</span>
        <div class="weui-cell__desc">`+ time + `</div>
    </span>
    <span class="weui-cell__ft">
    </span>
    </a>`;
}

function tpl_file(name, time, path, canDel) {
    return `<a class="weui-cell  weui-cell_access" href="javascript:void(0)" onclick="selectFile('` + path + `', ` + canDel + `)">
    <div class="weui-cell__hd"><img src="`+ ic_file + `" alt="" style="width: 32px; margin-right: 16px; display: block;"></div>
    <span class="weui-cell__bd">
        <span>`+ name + `</span>
        <div class="weui-cell__desc">`+ time + `</div>
    </span>
    </a>`;
}

function clear_list() {
    $('#file_list').html('');
}

function add_file(node) {
    $('#file_list').append(node);
}

let ic_dir = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA7AAAAOwBeShxvQAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAIoSURBVFiF7Ze/T9RgGMc/T9/WO+74IXdRNKCJxETPQaOJi8LoxiouTurmv+DAYvwH3JzVhOhgSHByMTqY6GBCCBFMcJFwURC4O1rbvo8DoIg9rvHuYOGTdGjfb/J88vZpn1Z0bKw7JjsiaCcJqMiCKQ1OyuhonLTeLG5MZlLQq/UCooqd/nwfuNcOAUfgSorcbR0fN20RACRF7hgzc0PtEJB47IGmCSq8EXjbOKjG+VG54Buv531puK/WkfO2L0cY+ZI5ksVfD84vTt91U5vCEBvH7qxU0GqV18PX+Xg2+e6GFmZXhPlc3zOX5dW0Do2xMbIeEBuXmVMX68Y8BwxQzhXFlWqtdQKbzA+cIch0pMq630vgX55DTJgY+Bn18ql8hyAqphb4VjjeMLPVeG722nOKztddw0EPTARPUws0ohqB3TRw8w2KAwyaCRara/i2u+niCqxHf85TPQWe+Jw2L3kX3GhaYCdip5xU74FV28dSPNB8QZR8VEbCiCe1h+kFWopCVLH42oWz58Xh98s/y9o+CWzjQOBA4EBg3wX+mgWxdwlrTtSJRrjBK0T99glUC49R52jdcG75Fp7/on0C4BGGIeWFMtsHRL4zT2/hMCoerebfHkgYTWm+2/+XHTsQ4h3y6D/ZnxgWjRKvt0wgv3QTa+rN/I0mbDVipxxLe3c5kWjNgqIOSOO/nRajMVu99kF0hi4iM4LQtSfVrWbigHMa21k35NEvWSq4Cnb1Ay8AAAAASUVORK5CYII=';
let ic_file = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAA7AAAAOwBeShxvQAAABl0RVh0U29mdHdhcmUAd3d3Lmlua3NjYXBlLm9yZ5vuPBoAAAJdSURBVFiF7ZfLaxNRFMa/c+8MbZO0xAgmGIIxUmtsQCkobsSNO12I7v0PXAjuBDeCCN0I/gvdu3HhQlHBBwouTA1UUoulKXmYSDp59TEzx4XUpmFm0tsMTRd+u5l7uN9vvnvunRmCh+r1+km2+CWAlFedm5byP79PIHRx8tKk4VYjvCawTfvafs0BoN1qThWNYn7h3cK4EgDPI23P07Ox5pM7+zXfVqPRPFbulPO517nQngBmv3JwkwIPQXQDXDs9KAAANI1WtLpVXcx9yEU8AWY/WU95nRvL1tVbfhgLKXcgGq1oZe3XajabPdJdo3VfMOg2AAL7YQ8kUyewIiQs0wQAbG5sjHbqnUUARx0BAMi/IP4oEAxganpnFcuFEggU7q7x3AUHoaED9C6Bp5gZlmUpGQgSENL9OR0B2KUJCssFFAslJQAShPMXzkHX9b0DuCmRTCCRTCgB9JPzSeirhbeUEjDqBgyjoWQgiBCLxyCEcx8494DLZLZtwzbVmpAFeUaqlEA4EkY4Eu5fqKBDeg64bsNVVEoVJQMCITOTga47WznfJefJ4onjiMWjygBSk67jSgcRCYImlNqmr4beA0M/iP4ncDgT+BfBAUThmQDbpv+OPZ+8ngBa+xWEWfbFt9PqgJmh6/r7XR5OxduIkpcw9uMsWE4MZM7QMKoFq2uBF2dS6VO1vgArWzPIjDwHSUCOA2DXf0sFit9z6XS61nu7F8AEgLfrD/CtfQUhWR3YNzZS+ngzdPc+ps03TuO7Xjuzn61HzHQPDL1pAvaAu4AZJYvo+uPL9MWt5g/5NsVsHsMO8wAAAABJRU5ErkJggg==';

let current_root = '';
let current_parent = '';
let current_remote = '';
let current_file = '';

function selectFile(path, canDel) {
    current_file = path;
    if (canDel)
        $("#delFileBtn").show();
    else
        $("#delFileBtn").hide();
    // $("#fileUrl0")[0].value = current_remote.replace('clan://', 'http://') + 'file/' + current_file;
    $("#fileUrl1")[0].value = "clan://localhost/" + current_file;
    $("#fileUrl2")[0].value = current_remote + current_file;
    $("#fileInfoDialog").show();
}

function fileToApi(type) {
    if (type === 1) {
        doAction('api', { url: "clan://localhost/" + current_file });
    } else {
        doAction('api', { url: current_remote + current_file });
    }
}

function hideFileInfo() {
    $("#fileInfoDialog").hide();
}

function listFile(path) {
    console.log(path);
    // $('#loadingToast').show();
    $.get('/file/' + path, function (res) {
        let info = JSON.parse(res);
        let parent = info.parent;
        let canDel = info.del === 1;
        current_root = path;
        current_parent = parent;
        current_remote = info.remote;
        let array = info.files;
        if (path === '' && array.length == 0)
            warnToast('读取本地文件失败，可能没有存储权限');
        clear_list();
        if (parent !== '.')
            add_file(tpl_top(parent));

        if (canDel) {
            $('#delCurFolder').show();
        } else {
            $('#delCurFolder').hide();
        }

        array.forEach(node => {
            if (node.dir === 1) {
                add_file(tpl_dir(node.name, node.time, node.path));
            } else {
                add_file(tpl_file(node.name, node.time, node.path, canDel));
            }
        });
        $('#loadingToast').hide();
    })
}

function warnToast(msg) {
    $('#warnToastContent').html(msg);
    $('#warnToast').show();
    setTimeout(() => {
        $('#warnToast').hide();
    }, 1000);
}

function uploadFile() {
    $('#file_uploader').click();
}

function uploadTip() {
    let files = $('#file_uploader')[0].files;
    if (files.length <= 0)
        return false;
    let tip = '';
    for (var i = 0; i < files.length; i++) {
        tip += (files[i].name) + ',';
    }
    $('#uploadTipContent').html(tip);
    $('#uploadTip').show();
}

function doUpload(yes) {
    $('#uploadTip').hide();
    if (yes == 1) {
        let files = $('#file_uploader')[0].files;
        if (files.length <= 0)
            return false;
        var formData = new FormData();
        formData.append('path', current_root);
        for (i = 0; i < files.length; i++) {
            formData.append("files-" + i, files[i]);
        }
        $('#loadingToast').show();
        $.ajax({
            url: '/upload',
            type: 'post',
            data: formData,
            processData: false,
            contentType: false,
            complete: function () {
                $('#loadingToast').hide();
                listFile(current_root);
            }
        });
    }
}

function newFolder() {
    $('#newFolder').show();
}

function doNewFolder(yes) {
    $('#newFolder').hide();
    if (yes == 1) {
        let name = $('#newFolderContent')[0].value.trim();
        if (name.length <= 0)
            return false;
        $('#loadingToast').show();
        $.post('/newFolder', { path: current_root, name: '' + name }, function (data) {
            $('#loadingToast').hide();
            listFile(current_root);
        });
    }
}


function delFolder() {
    $('#delFolderContent').html('是否删除 ' + current_root);
    $('#delFolder').show();
}

function doDelFolder(yes) {
    $('#delFolder').hide();
    if (yes == 1) {
        $('#loadingToast').show();
        $.post('/delFolder', { path: current_root }, function (data) {
            $('#loadingToast').hide();
            listFile(current_parent);
        });
    }
}

function delFolder() {
    $('#delFolderContent').html('是否删除 ' + current_root);
    $('#delFolder').show();
}

function doDelFolder(yes) {
    $('#delFolder').hide();
    if (yes == 1) {
        $('#loadingToast').show();
        $.post('/delFolder', { path: current_root }, function (data) {
            $('#loadingToast').hide();
            listFile(current_parent);
        });
    }
}

function delFile() {
    hideFileInfo();
    $('#delFileContent').html('是否删除 ' + current_file);
    $('#delFile').show();
}

function doDelFile(yes) {
    $('#delFile').hide();
    if (yes == 1) {
        $('#loadingToast').show();
        $.post('/delFile', { path: current_file }, function (data) {
            $('#loadingToast').hide();
            listFile(current_root);
        });
    }
}

function showPanel(id) {
    let tab = $('#tab' + id)[0];
    $(tab).attr('aria-selected', 'true').addClass('weui-bar__item_on');
    $(tab).siblings('.weui-bar__item_on').removeClass('weui-bar__item_on').attr('aria-selected', 'false');
    var panelId = '#' + $(tab).attr('aria-controls');
    if (id === 3 && current_remote.length === 0) {
        listFile('')
    }
    $(panelId).css('display', 'block');
    $(panelId).siblings('.weui-tab__panel').css('display', 'none');
}

$(function () {
    $('.weui-tabbar__item').on('click', function () {
        showPanel(parseInt($(this).attr('id').substr(3)));
    });
});

var url = window.location.href;
if (url.indexOf('push.html') > 0)
    showPanel(2);
else if (url.indexOf('api.html') > 0)
    showPanel(3);
else if (url.indexOf('all.html') > 0)
    showPanel(3);
else
    showPanel(1);
