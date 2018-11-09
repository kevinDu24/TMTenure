$(function(){
	$.extend($.validator.messages, {
		required: "必填字段",
		maxlength: $.validator.format("内容过长"),
		number: "请输入数字"
	});
	$(':file').change(function(){
        var file = this.files[0];
        name = file.name;
        size = file.size;
        type = file.type;
        //your validation
    });

    $("#upload").click(function(){
        var formData = new FormData($('#frontImg').val());
        $.ajax({
            url: '/files?type=cmdImg',  //server script to process data
            type: 'POST',
            xhr: function() {  // custom xhr
                myXhr = $.ajaxSettings.xhr();
                return myXhr;
            },
            //Ajax事件
            // Form数据
            data: formData,
            //Options to tell JQuery not to process data or worry about content-type
            cache: false,
            contentType: false,
            processData: false,
            success: function(data){
                if("SUCCESS" == data.status){
                    alert("上传成功！");
                   var frontImg = data.data.url;
                }else{
                    alert("上传失败！" + data.error + "！");
                }
            },
            error : function(data){
                alert("系统异常！");
            }
        });
    });
    $("#uploadFile").click(function(){
            var formData = new FormData($('#behindImg'));
            $.ajax({
                url: '/files?type=cmdImg',  //server script to process data
                type: 'POST',
                xhr: function() {  // custom xhr
                    myXhr = $.ajaxSettings.xhr();
                    return myXhr;
                },
                //Ajax事件
                // Form数据
                data: formData,
                //Options to tell JQuery not to process data or worry about content-type
                cache: false,
                contentType: false,
                processData: false,
                 success: function(data){
                if("SUCCESS" == data.status){
                    alert("上传成功！");
                   var behindImg = data.data.url;
                }else{
                    alert("上传失败！" + data.error + "！");
                }
                },
                error : function(data){
                    alert("系统异常！");
                }
            });
        });
    $("#addInfoForm").validate({
			rules: {
				applyno: {required:true,number:true,maxlength:50},
				name: {required:true, maxlength:20},
				csrq: {required:true, maxlength:20,isCSRQ:true},
				hklx: {required:true, maxlength:20},
				khxl: {required:true, maxlength:50},
				gcmd: {required:true, maxlength:50},
				ywfc: {required:true, maxlength:20},
				sfyjsz: {required:true, maxlength:20},
				jszdah: {required:true, maxlength:50},
				clsydsf: {required:true, maxlength:20},
				clsydcs: {required:true, maxlength:20},
				zjlx: {required:true},
				zjhm: {required:true, minlength:1,maxlength:20},
				frontImg: {required:true},
				behindImg: {required:true},
				driveLicenceFrontImg: {required:true},
                driveLicenceBehindImg: {required:true},
                otherFile: {required:false},
				sjhm: {required:true,number:true,minlength:11,maxlength:11},
				hyzk: {required:true},
				gzdwmc: {required:true, maxlength:100},
				dwdz: {required:true, maxlength:100},
				sjjzdz: {required:true, maxlength:100},
				hjszdz: {required:true, maxlength:100},
				poxm: {required:true, maxlength:20},
				zjlx_po: {required:true},
				zjhm_po: {required:true, minlength:1,maxlength:20},
				gzdwmc_po: {required:true, maxlength:100},
				dwdz_po: {required:true, maxlength:100},
				jjlxrxm_1: {required:true, maxlength:20},
				jjlxrhm_1: {required:true,number:true, minlength:11, maxlength:11},
				ysqrgx_1: {required:true},
				jjlxrxm_2: {required:true, maxlength:20},
				jjlxrhm_2: {required:true, number:true, minlength:11,maxlength:11},
				ysqrgx_2: {required:true},
			},
			submitHandler: function() {
			 var obj = $("#addInfoForm").serialize()
                if(!$('#idCardFrontImg').val()  || !$('#idCardBehindImg').val() || !$('#driveLicenceFront').val() || !$('#driveLicenceBehind').val()){
                    alert("提交失败！请检查身份证、驾驶证照片是否已上传!");
                     return;
                }
			    console.log(obj);
				$.ajax({
     					url: "/addInfo/addCustInfo" ,
     					data: $("#addInfoForm").serialize(),
   					    type: 'GET', 
     					success: function(data){
     						if("SUCCESS" == data.status){     							
								alert("已提交成功！客服会尽快与您联系！");
     						}else{
								alert("提交失败！" + data.error + "！");
     						}
     					},
     					error : function(data){
							alert("提交失败！请稍后重试！");
     					}
				});					
			}
		});
});

//出身日期格式验证
jQuery.validator.addMethod("isCSRQ", function(value, element) {   
	var DATE_FORMAT = /^[0-9]{4}-[0-1]?[0-9]{1}-[0-3]?[0-9]{1}$/;
		if(DATE_FORMAT.test(value)){
 			return true;
   		} else {
   			return false;
		}
}, "出生日期格式：'YYYY-mm-dd'");


function changeHYZK(){
	var hyzk = $("#hyzk").val();
	if("已婚有子女" == hyzk || "已婚无子女" == hyzk){
		showPo();
		$("#po").show();
		$("#po_title").show();
	}else if("未婚" == hyzk || "离异" == hyzk){
		hidePo();
		$("#po").hide();
		$("#po_title").hide();
	}else{
		hidePo();
		$("#po").hide();
		$("#po_title").hide();
	}
}

function showPo(){
	$("#poxm").val("");
	
	$("#po_zjlx_po").val("")
	$("#po_zjlx_po").removeAttr("selected");
	
	$("#zjhm_po").val("");
	$("#gzdwmc_po").val("");
	$("#dwdz_po").val("");
}

function hidePo(){
	$("#poxm").val("poxm");
	
	$("#po_zjlx_po").val("po_zjlx_po")
	$("#po_zjlx_po").attr("selected","selected")
	
	$("#zjhm_po").val("zjhm_po");
	$("#gzdwmc_po").val("gzdwmc_po");
	$("#dwdz_po").val("dwdz_po");
}


//身份证背面
 function ajaxFileUpload() {
     var formData = new FormData($('#behindImg').val());
        $.ajaxFileUpload({
            url : '/files?type=cmdImg',
            secureuri : false,
            fileElementId : 'behindImg',
            dataType : 'json',
            data : formData,
            success : function(data) {
               if("SUCCESS" == data.status){
                 alert("上传成功！");
                 var behindImg = data.data.url;
                 $("#idCardBehindImg").val(behindImg);
                }else {
                  alert(data.error );
               }
            },
            error : function(data) {
                alert('上传出错');
            }
        })
        return false;
    }
    //身份证正面
function fileUpload() {
    var formData = new FormData($('#frontImg').val());
    $.ajaxFileUpload({
        url : '/files?type=cmdImg',
        secureuri : false,
        fileElementId : 'frontImg',
        dataType : 'json',
        data : formData,
        success : function(data, status, e) {
        if("SUCCESS" == data.status){
        alert("上传成功！");
        var url = data.data.url;
         $("#idCardFrontImg").val(url);
         }else {
           alert(data.error );
         }
        },
        error : function(data, status, e) {
            alert('上传出错');
        }
    })
    return false;
}
//驾驶证正面
function driveLicenceFrontImgUpload(){
     var formData = new FormData($('#driveLicenceFrontImg').val());
          $.ajaxFileUpload({
              url : '/files?type=cmdImg',
              secureuri : false,
              fileElementId : 'driveLicenceFrontImg',
              dataType : 'json',
              data : formData,
              success : function(data) {
                 if("SUCCESS" == data.status){
                   alert("上传成功！");
                   var url = data.data.url;
                   $("#driveLicenceFront").val(url);
                  }else {
                    alert(data.error );
                 }
              },
              error : function(data) {
                  alert('上传出错');
              }
          })
          return false;
}

//驾驶证副本
function driveLicenceBehindImgUpload(){
        var formData = new FormData($('#driveLicenceBehindImg').val());
          $.ajaxFileUpload({
              url : '/files?type=cmdImg',
              secureuri : false,
              fileElementId : 'driveLicenceBehindImg',
              dataType : 'json',
              data : formData,
              success : function(data) {
                 if("SUCCESS" == data.status){
                   alert("上传成功！");
                   var url = data.data.url;
                   $("#driveLicenceBehind").val(url);
                  }else {
                    alert(data.error );
                 }
              },
              error : function(data) {
                  alert('上传出错');
              }
          })
          return false;
}

//其他文件
function otherFileUpload(){
            var xhr = new XMLHttpRequest();//第一步
            //定义表单变量
            var file = document.getElementById('otherFile').files;
            if(file.length > 5){
                 alert("文件上传过多，最多可上传5个！");
                 return;
            }
            //console.log(file.length);
            //新建一个FormData对象
            var formData = new FormData(); //++++++++++
            //追加文件数据
            for(i=0;i<file.length;i++){
                 formData.append("files", file[i]); //++++++++++
            }
            //formData.append("file", file[0]); //++++++++++

            //post方式
            xhr.open('POST', '/files/multifileUpload?type=cmdOtherImg'); //第二步骤
            //发送请求
            xhr.send(formData);  //第三步骤
            //ajax返回
            xhr.onreadystatechange = function(){ //第四步
        　　　　if ( xhr.readyState == 4 && xhr.status == 200 ) {
                    var str = xhr.responseText;
                    var data =  JSON.parse(str);
                    if(data.status == 'SUCCESS'){
                         alert("上传成功！");
                         var urls = data.data;
                        var url = '';
                        for(i=0;i<urls.length;i++){
                        if(i==0){
                            url = urls[i].url;
                        }else{
                            url = url +  ';' + urls[i].url;
                        }
                        }
                        console.log(url)
                        $("#otherFiles").val(url);
                    }else{
                         alert(data.error );
                    }
        　　　　}
        　　};
            //设置超时时间
            xhr.timeout = 100000;
            xhr.ontimeout = function(event){
        　　　　alert('请求超时！');
        　　}
//var formData = new FormData($('#batchUploadRiskFileForm')[0]);
//   $.ajaxFileUpload({
//       url : '/files/multifileUpload?type=cmdImg',
//       secureuri : false,
//       fileElementId : 'otherFile',
//       dataType : 'json',
//       data : formData,
//       success : function(data) {
//          if("SUCCESS" == data.status){
//            alert("上传成功！");
//            var otherFile = data.data.url;
//            $("#driveLicenceBehind").val(otherFile);
//           }else {
//             alert(data.error );
//          }
//       },
//       error : function(data) {
//           alert('上传出错');
//       }
//   })
//   return false;
}

