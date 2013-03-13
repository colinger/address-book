$(document).ready(function(){
	$("#tag-update").blur(function(){
		var tag = $("#tag-update").val();
		if(tag==""){
			return;
		}
		$.ajax({
			type: "POST", //用POST方式传输
			dataType: "json", //数据格式:JSON
			url: '/admin/game/tag', //目标地址
			data:{
                id: $("#id").val(),
                tag:$("#tag-update").val()
     		 },
			error: function (XMLHttpRequest, textStatus, errorThrown) { 
				
			},
			success: function (data,status){
				alert(data.GENERATED_KEY);
			}
		});	
	});
});