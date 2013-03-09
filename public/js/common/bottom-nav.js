(function (scriptId, styleVersion) {
// 公共组件：底部页脚通栏导航
// 在DOMContentLoaded加载完成时调。至少也要是body元素加载完成后调用。
//
// @param scriptId {String} 必选项 脚本在页面中引用的ID，
// @param styleVersion {String} 必选项 样式的版本号，用于样式更新清除CDN
//
// 事例，在页面中引用：
//      <script id="bottom-nav" src="http://www.woniu.com/includes/js/common/bottom-nav.js?clearCDN=2012.5.3"></script>
//      属性 id 必选项，的值为变量 scriptId, 默认为：'bottom-nav'
// HTML片段：
//      保存在 shellHtml 中。
// 样式文件：
//      http://www.woniu.com/includes/css/common/bottom-nav.css?clearCDN=styleVersion
//      更新样式，同时也要更新 styleVersion 变量。

    var doc = document,
        bodyElem = doc.body,
        headElem = doc.getElementsByTagName('head')[0],
        headerCss = doc.createElement('link'),
        headerHtml = doc.createDocumentFragment(),
        shellHtml = doc.createElement('div'),        
        pubFooter = document.getElementById(scriptId),
        public_footer_html = [];

    headerCss.setAttribute('rel', 'stylesheet');
    headerCss.setAttribute('type', 'text/css');
    headerCss.setAttribute('href', '/css/bottom-nav.css');
    headElem.appendChild(headerCss);

    // 添加通用导航的样式文件
	// pubFooter.onload 解决 webKit 不能正常触发 headerCss.onload 问题
    headerCss.onload = headerCss.onreadystatechange = pubFooter.onload = function(){
        if (!this.readyState || this.readyState === 'loaded' || this.readyState === 'complete') {
            headerCss.onload = headerCss.onreadystatechange = pubFooter.onload = null;
            
            // 借用innerHTML将通用导航的DOM添加到文档碎片中
            public_footer_html = [
				'<div id="pub_footer">',
				'<div class="pub_footer_in">',
				'<div class="pub_footer_in_left">',
				'<a target="_blank" title="游戏必杀网，玩的不是游戏，是成就！" href="#"></a>',
				'</div>',
				'<div class="pub_footer_in_right">',
				'<dl>',
				'<dd class="dd_1">',
				'<a target="_blank" href="#">关于游戏</a> | ',
				'<a target="_blank" href="#">About Game</a> | ',
				'<a target="_blank" href="#">服务条款</a> | ',
				'<a target="_blank" href="#">商务合作</a> | ',
				'<a href="#">网站导航</a> | <a href="#">版权声明</a>',
				'</dd>',
				'<dd class="dd_2">&copy; 2011  XXXXXX 版权所有</dd>',
				'<dd class="dd_2">',
				'<a target="_blank" href="#">文网文XXXXXX号</a> ',
				'<a target="_blank" href="#">互联网出版许可证</a> ',
				'<a target="_blank" href="#">XXXXXX</a> ',
				'<a target="_blank" href="#">XXXXXX</a> 文网游备字XXXXXX号',
				'</dd>',
				'</dl>',
				'<div class="pub_tb">',
				'</div>',
				'</div>',
				'</div>',
				'</div>'
            ];
            shellHtml.innerHTML = public_footer_html.join('');

            while (shellHtml.firstChild) {
                headerHtml.appendChild(shellHtml.removeChild(shellHtml.firstChild));
            }
            
            // 将页脚导航的DOM结构添加为Body元素的最后一个子节点
            bodyElem.appendChild(headerHtml);
        }
    };
})('bottom-nav', '1.0.0');