# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
* What to know
  * ServerSocket이 사용자의 요청이 오기 전까지 대기 상태에 있도록 하는 역할
  * 요청이 오면 새로운 스레드를 실행하는 멀티스레드 프로그래밍 지원(Thread를 상속 받은 RequestHandler)
  * 요청메세지를 파싱해서 맞는 응답을 전달하는 방법
    * 요청메세지의 첫 줄을 파싱해서 url을 뽑아내고 webapp directory 아래 해당 파일을 찾아 응답의 body에 넣어서 보냄

### 요구사항 2 - get 방식으로 회원가입
* What to know
  * 요청 url에 parameter를 붙여서 보내면 파싱을 해서 데이터를 전달

### 요구사항 3 - post 방식으로 회원가입
* What to know
  * POST로 요청을 하면 form data가 body를 통해 전달

### 요구사항 4 - redirect 방식으로 이동
* What to know
  * 응답의 status code를 302로 보내줄 경우 redirect 되는 body의 내용 뿐만 아니라 response header에 location도 바꿔줘야 url이 변경된다.

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 
