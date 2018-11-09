(function($){
    $.fn.numberRock=function(options){
        var defaults={
            min:0,
            max:1000,
            speed:50
        };
        var opts=$.extend({}, defaults, options);
        var div_by = 100,
            min=opts["min"],
            max=opts["max"],
            speed = Math.floor((max-min) / div_by),
            sum=0,
            $display = this,
            run_max = 1,
            int_speed = opts["speed"];
        var int = setInterval(function () {
            if (run_max <= div_by&&speed!=0) {
                if(speed < 0){
                    $display.text(sum = max);
                    run_max = div_by + 1;
                }else{
                    $display.text(sum=min + speed * run_max);
                    run_max++;
                }
            } else if (sum < max) {
                if(sum < min){
                    sum = min;
                }else if(sum < max){
                    $display.text(++sum);
                }
            } else {
                clearInterval(int);
            }
        }, int_speed);
    }
})(jQuery);