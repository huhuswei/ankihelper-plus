document.addEventListener('DOMContentLoaded', function() {
    var links = document.getElementsByTagName('a');
    for (var i = 0; i < links.length; i++) {
        links[i].addEventListener('long-press', function(e) {
            e.preventDefault();
            var selectedText = window.getSelection().toString();
            // 处理选中的文本
        });
    }
});