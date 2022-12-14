function shortreview(){

    let content = $("#content").val();
    let packageNum = $("#packagenum").val();
    let score =$("#result").text();

    if(content==""){
        alert("내용을 비워둘 수 없습니다.");
        return false;
    }

    $.ajax({
        url: '/package/shortReview/create',
        type: 'post',
        data: {
            content : $("#content").val(),
            packageNum : $("#packagenum").val(),
            score : score,
        },
        success : function(data) {

            document.getElementById("content").value = '';
            document.querySelector(`.star span`).style.width = `0%`;
            document.querySelector(`#result`).innerText = "별점을 드래그 해주세요";
            $('#shortReview').replaceWith(data);


        },
        error: function(xhr, status, error) {
            alert('error');
        }

    })

}