/**
 * [description] px 转 rem
 * @param  {[type]} doc [description] document
 * @param  {[type]} win [description] window
 * @return {[type]}     [description]
 */
(function(doc, win) {
  var docEl = doc.documentElement,
    resizeEvt = 'orientationchange' in window ? 'orientationchange' : 'resize',
    recalc = function() {
      var clientWidth = docEl.clientWidth
      if (!clientWidth) return
      docEl.style.fontSize = 20 * (clientWidth / 320) + 'px'
    };
  if (!doc.addEventListener) return
  win.addEventListener(resizeEvt, recalc, false)
  doc.addEventListener('DOMContentLoaded', recalc, false)
})(document, window)
