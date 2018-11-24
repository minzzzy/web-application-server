package webserver;

import db.DataBase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final String HOME_PATH = "./webapp";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = bufferedReader.readLine();
            if (line == null) {
                return;
            }
            log.debug("header : {}", line);

            HttpRequestUtils.StartLine startLine = HttpRequestUtils.parseStartLine(line);

            DataOutputStream dos = new DataOutputStream(out);
            ArrayList<String> lines = getHeader(bufferedReader);
            String path = HttpRequestUtils.getPath(startLine.getUrl());

            if (path.equals("/user/create")) {
                String requestBody = IOUtils.readData(bufferedReader, findContentLength(lines));
                saveUser(getParams(requestBody));
                log.debug("requestBody : {}", requestBody);

                response302Header(dos, "/index.html", false);
            } else if (path.equals("/user/login")) {
                String requestBody = IOUtils.readData(bufferedReader, findContentLength(lines));
                boolean logined = successLogin(getParams(requestBody));
                String redirectPath = (logined) ? "/index.html" : "/user/login_failed.html";
                response302Header(dos, redirectPath, logined);
            } else if (path.equals("/user/list")) {
                boolean logined = isLogined(findCookie(lines));
                if (!logined) {
                    response302Header(dos, "/user/login.html", logined);
                } else {
                    byte[] body = userListHtml(DataBase.findAll()).getBytes();
                    response200Header(dos, body.length, path);
                    responseBody(dos, body);
                }
            } else {
                byte[] body = getBody(path);
                response200Header(dos, body.length, path);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private boolean isLogined(Map<String, String> cookie) {
        if (cookie != null) {
            return Boolean.parseBoolean(cookie.get("logined"));
        }
        return false;
    }

    private Map<String, String> getParams(String requestBody) {
        if (requestBody.isEmpty()) {
            return null;
        }
        return HttpRequestUtils.parseQueryString(requestBody);
    }

    private void saveUser(Map<String, String> params) {
        if (!params.containsKey("userId") || !params.containsKey("password") || !params.containsKey("name") || !params.containsKey("email")) {
            return;
        }
        DataBase.addUser(new User(params.get("userId"), params.get("password"), params.get("name"), params.get("email")));
        log.debug("Save user: {}", DataBase.findUserById(params.get("userId")));
    }

    private boolean successLogin(Map<String, String> params) {
        User user = DataBase.findUserById(params.get("userId"));
        if (user != null && user.getPassword().equals(params.get("password"))) {
            return true;
        }
        return false;
    }

    private ArrayList<String> getHeader(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        ArrayList<String> lines = new ArrayList<>();
        while (!"".equals(line)) {
            line = bufferedReader.readLine();
            log.debug("header: {}", line);
            lines.add(line);
        }
        return lines;
    }

    private int findContentLength(ArrayList<String> lines) {
        return lines.stream().map(HttpRequestUtils::parseHeader)
                .filter(pair -> pair != null && pair.getKey().equals("Content-Length"))
                .map(pair -> Integer.parseInt(pair.getValue()))
                .findAny()
                .orElse(0);
    }

    private Map<String, String> findCookie(ArrayList<String> lines) {
        return lines.stream().map(HttpRequestUtils::parseHeader)
                .filter(pair -> pair != null && pair.getKey().equals("Cookie"))
                .map(pair -> HttpRequestUtils.parseCookies(pair.getValue()))
                .findAny()
                .orElse(null);
    }

    private byte[] getBody(String url) throws IOException {
        Path path = new File(HOME_PATH + url).toPath();
        if (path.toString().equals(HOME_PATH)) {
            return "Hello world".getBytes();
        }
        return Files.readAllBytes(path);
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String path) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(getContentType(path));
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private String getContentType(String path) {
        String fileExtension = path.replaceAll("^.*\\.(.*)$", "$1");
        if (fileExtension.equals("css")) {
            return "Content-Type: text/css\r\n";
        }
        return "Content-Type: text/html;charset=utf-8\r\n";
    }

    private void response302Header(DataOutputStream dos, String location, boolean isLogined) {
        try {
            dos.writeBytes("HTTP/1.1 302 FOUND \r\n");
            dos.writeBytes("Location: " + location + "\r\n");
            if (isLogined) {
                dos.writeBytes("Set-Cookie: logined=true\r\n");
            } else {
                dos.writeBytes("Set-Cookie: logined=false\r\n");
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
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
