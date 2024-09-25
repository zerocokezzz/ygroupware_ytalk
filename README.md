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

| 1. 회원가입 | 2. 로그인 |
| --- | --- |
| 메신저 서비스를 이용하기 위한 첫 단계입니다. | 등록된 계정으로 YGroupWare에 접속합니다. |
| ![제목 없는 동영상 - Clipchamp로 제작 (5)](https://github.com/user-attachments/assets/225a3a4f-d270-475f-bc87-580f350c8928) | ![제목 없는 동영상 - Clipchamp로 제작 (2)](https://github.com/user-attachments/assets/219de919-6fbf-4568-8552-ce3e39ac5f8f) |
| 3. 대화방 만들기 및 메시지 보내기 | 4. 알림 메시지 및 나가기 |
| 새로운 대화를 시작하고 메시지를 주고받습니다. | 알림 설정 및 대화방 나가기 기능을 사용합니다. |
| ![제목 없는 동영상 - Clipchamp로 제작](https://github.com/user-attachments/assets/18dbb253-ca0f-41b1-84a6-6669035d39e1) | ![제목 없는 동영상 - Clipchamp로 제작 (3)](https://github.com/user-attachments/assets/379d1ec9-4539-4c36-befb-f3f50c90645d) |
| 5. 로그아웃 | 6. 자동로그인 |
| 자동 로그인 기능은 사용자의 편의를 위해 제공되는 옵션입니다. | 로그아웃 기능을 사용하여 안전하게 서비스를 종료합니다. |
| ![제목 없는 동영상 - Clipchamp로 제작 (1)](https://github.com/user-attachments/assets/0eecc90a-1081-43e0-9942-3898571d4907) | ![제목 없는 동영상 - Clipchamp로 제작 (4)](https://github.com/user-attachments/assets/ced97ad1-91b4-4387-a6ff-5d8140aafecd) |







