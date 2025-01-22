# tdd-project-ex
TDD 방식을 적용하여 '오늘의 운세' 프로젝트 재구현

<br/>

## 기능 정의
### 사용자 인터페이스 동작 로직
1. 사용자 이름 입력 시:
   - 입력 값이 있으면 확인 버튼(btn_submit) 활성화
   - 입력 값이 없으면 버튼 비활성화
2. 확인 버튼 클릭 시:
   - 운세 텍스트(txt_fortune)와 다시하기 버튼(btn_reset) 표시
   - 확인 버튼(btn_submit) 숨김
3. 다시하기 버튼 클릭 시:
   - 입력 필드 초기화
   - 운세 텍스트와 다시하기 버튼 숨김
   - 확인 버튼 다시 활성화
  
### 데이터 처리 및 시스템 로직
1. 사용자 이름 입력:
   - input_txt_name.length > 0:
     - True: btn_submit.enable = true
     - False: btn_submit.enable = false
2. 확인 버튼 클릭 시:
   - 데이터베이스(DB)에서 사용자 정보 확인:
     - DB에 정보 없음:
       - 서버에 데이터를 요청하여 DB에 저장
     - DB에 정보 있음:
       - DB에 요청하여 데이터를 가져옴
   - 데이터를 가져오면:
     - txt_fortune.text = 운세 데이터
     - btn_reset.visibility = visible
     - btn_submit.visibility = gone
3. 다시하기 버튼 클릭:
   - 입력 필드 초기화
   - 버튼 및 텍스트 초기 상태로 복구
     - input_txt_name.text = ""
     - btn_submit.enable = false
     - txt_fortune.visibility = gone
     - btn_reset.visibility = gone