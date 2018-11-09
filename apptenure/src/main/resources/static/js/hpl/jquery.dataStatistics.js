$.fn.dataStatistics = function(options){
	options = $.extend({  
	    min  : 0,       //初始数值
	    max  : 999, //最大数字
	    time : 3000,  //时长
	    len:3 //数字是几位数
	},options || {});
	var ths = this;//解决this指向问题
// 初始化---------------------------------------start
  	var el = ths.find('.set_last');
  	var html = '<div class="digit">' +
					'  <div class="digit_top">' +
						'   <span class="digit_wrap"></span>' +
					'  </div>' +
					'  <div class="shadow_top"></div>' +
					'  <div class="digit_bottom">' +
						'   <span class="digit_wrap"></span>' +
					'  </div>' +
					'  <div class="shadow_bottom"></div>' +
				'</div>';
  	//初始化值
  	var nowNums=zfill(options.min, options.len).toString().split("");
  	//补0
  	function zfill(num, size) {
		var s = "000000000" + num;
		return s.substr(s.length-size);
	}
	ths.find('.digit_set').each(function() {
       	for(i=0; i<=9; i++) {
           	$(this).append(html);
           	currentDigit = $(this).find('.digit')[i];
           	var imgSrc = "<img src='img/hpl/"+ i +".png'/>";
           	$(currentDigit).find('.digit_wrap').append(imgSrc);
       	}
    });
    //初始化数值填入
    $.each(nowNums, function(index,val) {
      	var set =ths.find('.digit_set').eq(index);
      	var i =parseInt(val)
      	set.find('.digit').eq(i).addClass('active');
      	set.find('.digit').eq(i+1).addClass('previous');
    });
//初始化---------------------------------------end
      
    //执行			
	function run(){
		var difference =options.max-options.min;//要执行动画的次数
		var t = options.time/difference;//每次要执行动画的时间
		//后一位数
		for (var i = 0;i < difference; i++){
			window.setTimeout(increase,t*i);
		}
		//increase();
	  	function increase() {
	  		//执行次数为0时,停止执行
	  		if (difference<1) {
	        	clearInterval(timer1);
	        	return false
	     	}
	  		difference--;
	        //翻页动画
	        var current = el.find('.active'),
	            previous = el.find('.previous');
	        current.attr("z-index","1");
	        previous.removeClass('previous');
	        current.removeClass('active').addClass('previous');

	        if (current.next().length == 0) {
	          el.find('.digit:first-child').addClass('active');
	          var prev = el.prev();
	          prevNumber(prev);
	        } else {
	          current.next().addClass('active');
	        }
	    }
	  	//var timer1 = setInterval(increase,t);
	}
  	//当数字翻到9的时候，前一位数执行一次动画
  	function prevNumber(ths){
  		var current = ths.find('.active'),
            previous = ths.find('.previous');
        previous.removeClass('previous');
        current.removeClass('active').addClass('previous');
        
        if (current.next().length == 0) {
          ths.find('.digit:first-child').addClass('active');
          var prev = ths.prev();
          if (prev.length>0) {
          	prevNumber(prev);
          }
        } else {
          current.next().addClass('active');
        }
  	}
  	run();
};