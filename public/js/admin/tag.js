$(document).ready(function(){
	$("#tag-update").blur(function(){
		var tag = $("#tag-update").val();
		if(tag==""){
			return;
		}
		$.ajax({
			type: "POST", //��POST��ʽ����
			dataType: "json", //���ݸ�ʽ:JSON
			url: '/admin/game/tag', //Ŀ���ַ
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