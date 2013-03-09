(function (scriptId, styleVersion) {
// 公共组件：顶部通栏导航
// 在DOMContentLoaded加载完成时调。至少也要是body元素加载完成后调用。
//
// @param scriptId {String} 必选项 脚本在页面中引用的ID，
// @param styleVersion {String} 必选项 样式的版本号，用于样式更新清除CDN
//
// 事例，在页面中引用：
//      <script id="top-nav" src="http://www.woniu.com/includes/js/common/top-nav.js?clearCDN=2012.5.3"></script>
//      属性 id 必选项，的值为变量 scriptId, 默认为：'top-nav'
// HTML片段：
//      保存在 shellHtml 中。
// 样式文件：
//      http://www.woniu.com/includes/css/common/top-nav.css?clearCDN=styleVersion
//      更新样式，同时也要更新 styleVersion 变量。

    var doc = document,
        bodyElem = doc.body,
        headElem = doc.getElementsByTagName('head')[0],
        headerCss = doc.createElement('link'),
        headerHtml = doc.createDocumentFragment(),
        shellHtml = doc.createElement('div'),
        getId = function (id) {
            return document.getElementById(id);
        },
        contains = function (parentNode, childNode) {
            if (parentNode.contains) {
                return parentNode != childNode && parentNode.contains(childNode);
            } else {
                return !!(parentNode.compareDocumentPosition(childNode) & 16);
            }
        },
        toElem = function(event){
            var eve = event || window.event;
            return eve.relatedTarget || eve.toElement || eve.fromElement;
        },
        pubHeader = getId(scriptId),
        loginRegister = [],
        //username = $.cookie("SSOPrincipal"),
        public_header_html = [];

    headerCss.setAttribute('rel', 'stylesheet');
    headerCss.setAttribute('type', 'text/css');
    headerCss.setAttribute('href', '/css/top-nav.css');
    headElem.appendChild(headerCss);

    // 添加通用导航的样式文件
    // pubHeader.onload 解决 webKit 不能正常触发 headerCss.onload 问题
    headerCss.onload = headerCss.onreadystatechange = pubHeader.onload = function(){
        if (!this.readyState || this.readyState === 'loaded' || this.readyState === 'complete') {
            headerCss.onload = headerCss.onreadystatechange = pubHeader.onload = null;

            // 如果已登录，则显示用户名和注销
            //if (username) {
            //    username = username.replace(new RegExp("\"", 'g'), "");
            //    if(username.length > 3) {
        	//		var index = 3 - username.length;
        	//		var suffix = -3;
        	//		if(index > -3 ) {
        	//			suffix = index;
        	//		}
        	//		username = username.slice(0, 3) + '**' + username.slice(suffix);
        	//	}
            //    loginRegister.push('<a class="header_a1" href="http://passport.woniu.com/">'+ username.toUpperCase() +'</a>');
            //    loginRegister.push('&nbsp;&nbsp;&nbsp;&nbsp;<a class="header_a1" style="color:#999;" href="https://sso.woniu.com/logout?service=http://www3.woniu.com/">退出</a>');
            //} else {
            //    loginRegister.push('<a class="header_a1" href="http://register.woniu.com">注册</a>');
            //    var currentUrl = doc.location.href;
            //    loginRegister.push('&nbsp;&nbsp;&nbsp;&nbsp;<a class="header_a1" href="https://sso.woniu.com/login?service='+currentUrl+'">登录</a>');
            //}

            // 借用innerHTML将通用导航的DOM添加到文档碎片中
            public_header_html = [
                '<div id="tl_public_header">',
                '<div class="tl_public_header_in">',
                '<div class="tl_header_left">',
                '<div class="tl_header_logo"><a href="http://www.woniu.com/" target="_blank" title="蜗牛网 - 玩的不是游戏，是成就"></a></div>',
                '<div class="tl_game_list">',
                '<ul>',
                '<li><h3><a href="http://vc.woniu.com/" target="_blank" title="航海世纪 - 全球经典航海网游大作，亲身体验加勒比海盗快感！">航海世纪</a></h3></li>',
                '<li><h3><a href="http://5jq.woniu.com/" target="_blank" title="舞街区 - 最时尚的3D音乐舞蹈网游 — 最好玩的休闲游戏选择">舞街区</a></h3></li>',
                '<li>',
                '<h3><a href="http://tz.woniu.com/" target="_blank" title="天子 - 新一代穿越网游巨作！">天子</a></h3>',
                '<div class="tl_hot" title="热门游戏"></div>',
                '</li>',
                '<li>',
                '<h3><a href="http://9yin.woniu.com/" target="_blank" title="九阴真经 - 2011年最值得期待真武侠3D网络游戏">九阴真经</a></h3>',
                '<div class="tl_new" title="最新游戏"></div>',
                '</li>',
                '<li>',
                '<h3><a href="http://dg.woniu.com/" target="_blank" title="帝国文明 - 第二代网页游戏巅峰之作！">帝国文明</a></h3>',
                '<div class="tl_hot" title="热门游戏"></div>',
                '</li>',
                '<li>',
                '<h3><a href="http://x.woniu.com/" target="_blank" title="X幻想-畅爽体验-秒爱上战斗！">X幻想</a></h3>',
                '<div class="tl_new" title="最新游戏"></div>',
                '</li>',
                '<li>',
                '<h3><a href="http://webgame.woniu.com?from=GWHEADER" target="_blank" title="页游中心 - 中国第一网页游戏平台">页游中心</a></h3>',
                '<div class="tl_new" title="最新游戏"></div>',
                '</li>',
                '</ul>',
                '</div>',
                '<div class="tl_game_more" id="tl_game_more_hook"><a href="#" id="tl_game_more_btn">更多游戏</a></div>',
                '</div>',
                '<div class="tl_header_right">',
                '<div class="tl_header_right_in">',
                '<dl>',
                '<dd>' + loginRegister.join('') + '</dd>',
                '<dd><a class="header_a2" href="http://imprest.woniu.com/imprest/imprest_main.html">充值</a></dd>',
                '<dd id="tl_more_service_btn_pre"><a class="header_a3" href="#" id="tl_more_service_btn">蜗牛服务</a></dd>',
                '</dl>',
                '</div>',
                '</div>',
                '</div>',
                '</div>',
                '<div class="clear"></div>',
            ];
            shellHtml.innerHTML = public_header_html.join('');

            while (shellHtml.firstChild) {
                headerHtml.appendChild(shellHtml.removeChild(shellHtml.firstChild));
            }

            // 将通用导航的DOM结构添加为文档的最前面
            bodyElem.insertBefore(headerHtml, bodyElem.firstChild);

            // 给更多按钮添加显示、隐藏详细内容
            var moreGameBtn = getId('tl_game_more_btn'),
                moreGameObj = getId('tl_more_game'),
                moreServiceBtn = getId('tl_more_service_btn'),
                moreServiceObj = getId('tl_more_service'),
                moreServiceBtnPre = getId('tl_more_service_btn_pre');

            // 更多游戏

            // 更多服务
            
        }
    };

})('top-nav', '1.0.0');