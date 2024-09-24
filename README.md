## 프로젝트 소개

이 프로젝트의 주요 구성 요소는 다음과 같습니다:

- **Spring Boot:** 애플리케이션의 기본 프레임워크로 사용되어 빠른 개발과 간편한 설정을 제공합니다.
- **Spring Data JPA:** 데이터베이스 작업을 간소화하고 객체-관계 매핑(ORM)을 지원합니다.
- **REST API:** 클라이언트와 서버 간의 효율적인 데이터 통신을 위해 구현되었습니다.
- **WebSocket:** 실시간 양방향 통신을 가능하게 하여 즉각적인 메시지 전송을 지원합니다.
- **PostgreSQL:** 안정적이고 확장 가능한 관계형 데이터베이스로 사용자 정보와 채팅 내역을 저장합니다.

이러한 기술 스택을 통해 안정적이고 효율적인 메신저 서비스를 구현하였습니다.

## 개발 기간 및 인원

- **기간:** 2024년 9월 9일 ~ 2024년 9월 23일 (2주간)
- **인원:** 개발자 1명
- [🌐 배포 링크](https://ytalk.azurewebsites.net)

## ERD 구조

## 기술 스택

![java.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/e9d9c4bd-a038-4c52-82d8-ff15d567e55b/java.png)

![springboot.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/fd4279fd-95e8-4706-981a-1b162e76d696/springboot.jpg)

![security.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/60a5b301-d2eb-4c1d-93b1-9a149834e0ea/security.png)

![springframewok.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/e6fe0c17-ea4e-4d11-ba61-2a283d304335/springframewok.png)

![gradle.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/4bddf815-71c7-4e3d-a2bd-7a6344bb92c1/gradle.png)

![springjpa.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/4a4608ec-fd61-4dff-95f1-276cc5cfb724/springjpa.png)

![postgreesql.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/d16f8de9-4e20-4f11-a4cc-ebb05a12b567/postgreesql.png)

![thymeleaf.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/6cf21893-3fbe-4489-9b80-8994f329ffa6/thymeleaf.png)

![github.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/a9d66c03-281d-4be5-87d3-f1112d4d3285/github.png)

![cloudtype.jpg](https://prod-files-secure.s3.us-west-2.amazonaws.com/8404a52b-c953-4172-a95f-8296b68f0606/81a4ed0c-8284-48fe-89c3-2d74c3caf6c6/cloudtype.jpg)

## 주요 기능

- **회원가입**: 사용자는 새로운 계정을 등록할 수 있으며, 중복된 이메일 또는 아이디를 방지하기 위한 유효성 검사를 포함합니다.
- **로그인**: 사용자가 등록된 계정으로 애플리케이션에 로그인할 수 있으며, 보안 인증을 통해 사용자 정보를 보호합니다.
- **메신저**:
    - **대화방 만들기**: 다른 사용자와 1:1 대화방을 생성하여 실시간 채팅을 시작할 수 있습니다.
    - **메시지 수신 및 발신**: WebSocket을 통해 실시간으로 메시지를 주고받을 수 있으며, 클라이언트는 서버에서 전송된 메시지를 즉시 수신합니다.
    - **읽지 않은 메시지 알림**: 사용자가 읽지 않은 메시지가 있을 경우 실시간 알림을 통해 확인할 수 있습니다.
- **로그아웃**: 사용자는 세션을 종료하고 안전하게 로그아웃할 수 있습니다.
- **자동로그인**: 사용자가 선택한 경우, 브라우저나 디바이스에서 자동으로 로그인하여 편리하게 애플리케이션에 접속할 수 있습니다.

## API 명세

| 도메인 | 엔드포인트 | HTTP 메서드 | 설명 |
| --- | --- | --- | --- |
| **비회원** | /non-member/pages-register | POST | 회원가입 |
|  | /non-member/terms | GET | 이용약관 |
|  | /non-member/collections | GET | 개인정보 수집 및 이용약관 |
|  | /non-member/name | GET | 이름 유효성 검사 |
|  | /non-member/emailAccount | GET | 이메일 계정 유효성 검사 |
|  | /non-member/id | GET | ID 유효성 검사 |
|  | /non-member/password | GET | 암호 유효성 검사 |
| **회원** | /talk/talk-index | GET | 메신저 접속 |
|  | /yet/my-profile | GET | 내정보 |
|  | /yet/users-profile | GET | 사용자 프로필 페이지 (개발 전) |
| **주소록** | /talk/partners | GET | 주소록 조회 |
|  | /partners/{id} | GET | 주소록 상세 조회 |
| **대화방** | /talk/chat-rooms | GET | 참여하고 있는 대화방 목록 조회 |
|  | /chat-room/partner/{partnerId} | GET | 대화방 찾기 및 생성 |
|  | /chat-room/messages/{chatRoomId} | GET | 대화 목록 및 상대방 ID 조회 |
|  | /chat-room/exit/{chatRoomId} | POST | 대화방 나가기 |
| **메시지** | /chat.sendMessage (WebSocket 사용) | @MessageMapping | 메세지 저장 |
|  | /chat-room/unread-messages | GET | 읽지 않은 메세지 조회 |
| **알림** | /chat-room/messages/mark-as-read | POST | 메세지 읽음 처리 |
|  | /api/notifications/unread-count | GET | 읽지 않은 메세지 카운트 |


## 메신저 사용 가이드

| 순서 | 설명 및 동영상 |
| --- | --- |
| 1. 회원가입 <br> 메신저 서비스를 이용하기 위한 첫 단계입니다. |   |
| 2. 로그인 | ytalk_로그인.mp4 <br> 등록된 계정으로 YGroupWare에 접속합니다. |
| 3. 대화방 만들기 및 메시지 보내기 | ytalk_대화방만들고메시지보내기.mp4 <br> 새로운 대화를 시작하고 메시지를 주고받습니다. |
| 4. 알림 메시지 및 나가기 | ytalk_알림메시지나가기.mp4 <br> 알림 설정 및 대화방 나가기 기능을 사용합니다. |
| 5. 로그아웃 | ytalk_로그아웃.mp4 <br> 로그아웃 기능을 사용하여 안전하게 서비스를 종료합니다. |
| 6. 자동로그인 | ytalk_자동로그인.mp4 <br> 자동 로그인 기능은 사용자의 편의를 위해 제공되는 옵션입니다. |
