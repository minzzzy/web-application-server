package webserver;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class ListUserController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        String cookieValue = request.getHeader("Cookie");
        boolean logined = isLogined(cookieValue);
        if (!logined) {
            response.sendRedirect("/user/login.html");
            return;
        }
        response.forward(request.getPath(), userListHtml(DataBase.findAll()).getBytes());
    }

    private boolean isLogined(String cookieValue) {
        Map<String, String> cookie = HttpRequestUtils.parseCookies(cookieValue);
        if (cookie == null) {
            return false;
        }
        return Boolean.parseBoolean(cookie.get("logined"));
    }

    private String userListHtml(Collection<User> users) {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder html = stringBuilder.append("<!DOCTYPE html>")
                .append("<html lang='kr'>")
                .append("<head>")
                .append("<meta http-equiv='content-type' content='text/html; charset=UTF-8'>")
                .append("<meta charset='utf-8'>")
                .append("<title>SLiPP Java Web Programming</title>")
                .append("<meta name='viewport' content='width=device-width, initial-scale=1, maximum-scale=1'>")
                .append("<link href='../css/bootstrap.min.css' rel='stylesheet'>")
                .append("<link href='../css/styles.css' rel='stylesheet'>")
                .append("</head>")
                .append("<body>")
                .append("<nav class='navbar navbar-fixed-top header'>")
                .append("<div class='col-md-12'>")
                .append("<div class='navbar-header'>")
                .append("<a href='../index.html' class='navbar-brand'>SLiPP</a>")
                .append("<button type='button' class='navbar-toggle' data-toggle='collapse' data-target='#navbar-collapse1'>")
                .append("<i class='glyphicon glyphicon-search'></i>")
                .append("</button>")
                .append("</div>")
                .append("<div class='collapse navbar-collapse' id='navbar-collapse1'>")
                .append("<form class='navbar-form pull-left'>")
                .append("<div class='input-group' style='max-width:470px;'>")
                .append("<input type='text' class='form-control' placeholder='Search' name='srch-term' id='srch-term'>")
                .append("<div class='input-group-btn'>")
                .append("<button class='btn btn-default btn-primary' type='submit'><i class='glyphicon glyphicon-search'></i></button>")
                .append("</div>")
                .append("</div>")
                .append("</form>")
                .append("<ul class='nav navbar-nav navbar-right'>")
                .append("<li>")
                .append("<a href='#' class='dropdown-toggle' data-toggle='dropdown'><i class='glyphicon glyphicon-bell'></i></a>")
                .append("<ul class='dropdown-menu'>")
                .append("<li><a href='https://slipp.net' target='_blank'>SLiPP</a></li>")
                .append("<li><a href='https://facebook.com' target='_blank'>Facebook</a></li>")
                .append("</ul>")
                .append("</li>")
                .append("<li><a href='../user/list.html'><i class='glyphicon glyphicon-user'></i></a></li>")
                .append("</ul>")
                .append("</div>")
                .append("</div>")
                .append("</nav>")
                .append("<div class='navbar navbar-default' id='subnav'>")
                .append("<div class='col-md-12'>")
                .append("<div class='navbar-header'>")
                .append("<a href='#' style='margin-left:15px;' class='navbar-btn btn btn-default btn-plus dropdown-toggle' data-toggle='dropdown'><i class='glyphicon glyphicon-home' style='color:#dd1111;'></i> Home <small><i class='glyphicon glyphicon-chevron-down'></i></small></a>")
                .append("<ul class='nav dropdown-menu'>")
                .append("<li><a href='../user/profile.html'><i class='glyphicon glyphicon-user' style='color:#1111dd;'></i> Profile</a></li>")
                .append("<li class='nav-divider'></li>")
                .append("<li><a href='#'><i class='glyphicon glyphicon-cog' style='color:#dd1111;'></i> Settings</a></li>")
                .append("</ul>")
                .append("<button type='button' class='navbar-toggle' data-toggle='collapse' data-target='#navbar-collapse2'>")
                .append("<span class='sr-only'>Toggle navigation</span>")
                .append("<span class='icon-bar'></span>")
                .append("<span class='icon-bar'></span>")
                .append("<span class='icon-bar'></span>")
                .append("</button>")
                .append("</div>")
                .append("<div class='collapse navbar-collapse' id='navbar-collapse2'>")
                .append("<ul class='nav navbar-nav navbar-right'>")
                .append("<li class='active'><a href='../index.html'>Posts</a></li>")
                .append("<li><a href='../user/login.html' role='button'>로그인</a></li>")
                .append("<li><a href='../user/form.html' role='button'>회원가입</a></li>")
                .append("<li><a href='#' role='button'>로그아웃</a></li>")
                .append("<li><a href='#' role='button'>개인정보수정</a></li>")
                .append("</ul>")
                .append("</div>")
                .append("</div>")
                .append("</div>")
                .append("<div class='container' id='main'>")
                .append("<div class='col-md-10 col-md-offset-1'>")
                .append("<div class='panel panel-default'>")
                .append("<table class='table table-hover'>")
                .append("<thead>")
                .append("<tr>")
                .append("<th>#</th> <th>사용자 아이디</th> <th>이름</th> <th>이메일</th><th></th>")
                .append("</tr>")
                .append("</thead>")
                .append("<tbody>");

        String usersHtml = users.stream()
                .map(user -> "<tr><th scope='row'>1</th> <td>" + user.getUserId() + "</td> <td>" + user.getName() + "</td> <td>" + user.getEmail() +
                        "</td><td><a href='#' class='btn btn-success' role='button'>수정</a></td></tr>")
                .collect(Collectors.joining());
        html.append(usersHtml);

        return html.append("</tbody>")
                .append("</table>")
                .append("</div>")
                .append("</div>")
                .append("</div>")
                .append("<script src='../js/jquery-2.2.0.min.js'></script>")
                .append("<script src='../js/bootstrap.min.js'></script>")
                .append("<script src='../js/scripts.js'></script>")
                .append("</body>")
                .append("</html>").toString();

    }
}