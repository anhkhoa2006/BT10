$(document).ready(function() {
    // Hiển thị thông tin người dùng đăng nhập thành công
    if ($('#profile').length) {
        if (!localStorage.getItem('token')) {
            alert("Sorry, you are not logged in.");
            window.location.href = "/login";
            return;
        }

        $.ajax({
            type: 'GET',
            url: '/users/me',
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            beforeSend: function(xhr) {
                if (localStorage.token) {
                    xhr.setRequestHeader('Authorization', 'Bearer ' + localStorage.token);
                }
            },
            success: function(data) {
                var json = JSON.stringify(data, null, 4);
                $('#profile').html('<h4>Chào mừng, ' + (data.fullName ? data.fullName : data.email) + '!</h4><pre>' + json + '</pre>');
                if (data.images) {
                    $('#images').attr('src', data.images).show();
                }
            },
            error: function(e) {
                var json = e.responseText;
                $('#profile').html('<div class="text-danger">Lỗi xác thực hoặc token hết hạn.</div>');
                alert("Sorry, you are not logged in or token expired.");
                localStorage.clear();
                window.location.href = "/login";
            }
        });
    }

    // Hàm đăng xuất
    $('#logout').click(function() {
        localStorage.clear();
        window.location.href = "/login";
    });

    // Hàm Login
    $('#login').click(function() {
        var email = $('#email').val();
        var password = $('#password').val();

        if (!email || !password) {
            alert("Vui lòng nhập Email và Password!");
            return;
        }

        var basicInfo = JSON.stringify({
            email: email,
            password: password
        });

        $.ajax({
            type: "POST",
            url: "/auth/login",
            dataType: 'json',
            contentType: "application/json; charset=utf-8",
            data: basicInfo,
            success: function(data) {
                localStorage.token = data.token;
                window.location.href = "/user/profile";
            },
            error: function(xhr) {
                var msg = "Login Failed";
                if (xhr.responseJSON && xhr.responseJSON.description) {
                    msg += ": " + xhr.responseJSON.description;
                }
                alert(msg);
            }
        });
    });
});
