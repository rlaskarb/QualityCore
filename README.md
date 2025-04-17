# 🍺BrauHaus

<p float="left">
  <img src="https://github.com/user-attachments/assets/f7a63cee-06f3-4eb1-a4fd-c5e62d9e3588" width="48%" />
  <img src="https://github.com/user-attachments/assets/83435cc2-e93a-4c3b-be88-d802bf66dd0c" width="48%" />
</p>

<br/>
프로젝트 기간 25.02.03 ~ 25.03.17 <br/>


---

👍 [meeting](https://github.com/QualityCore/QualityCore-backend/discussions/categories/meeting?discussions_q=) | 
👍 [Front-PR](https://github.com/QualityCore/QualityCore-frontend/pulls?q=is%3Apr+is%3Aclosed) | 
👍 [Back-PR](https://github.com/QualityCore/QualityCore-backend/pulls?q=is%3Apr+is%3Aclosed) | 
👍 [Front-Issues](https://github.com/QualityCore/QualityCore-frontend/issues?q=is%3Aissue%20state%3Aclosed) | 
👍 [Back-Issues](https://github.com/QualityCore/QualityCore-backend/pulls?q=is%3Apr+is%3Aclosed) | 

<br/>

---

## 🧭 빠른 이동

- [프로젝트 소개](#프로젝트-소개)
- [프로젝트 일정](#프로젝트-일정)
- [기술 스택](#기술-스택)
- [기능 설명](#기능-설명)
- [논리 & 물리 데이터 모델링](#논리--물리-데이터-모델링)

---

<a name = "프로젝트-소개"></a>
## 📌 프로젝트 소개

맥주 제조 공정의 생산부터 출하까지 전반적인 과정을 **직관적으로 관리할 수 있도록 설계된 ERP 프로젝트**입니다.<br/>

실제 양조 라인의 작업 흐름을 반영하여 **생산 계획 수립, 공정 제어, 실적 분석까지 통합 관리**할 수 있도록 구현하였습니다.<br/>

단순한 정보 나열이 아닌, **현장 중심의 실질적인 생산 관리 UX 구현**에 초점을 맞추었습니다. <br/> 
작업 지시, 공정 실행, 스케줄 확인 등 주요 업무는 사용자 친화적인 모달, 캘린더, 대시보드 등을 통해 처리됩니다.<br/>

<br/>
 
### 🍺 BrauHaus 주요 기능
 
#### ✅ 1. 생산 계획 수립 및 실행 흐름 지원
 - 월별/일별 생산계획을 등록하고 작업지시서 자동 발행  
 - 공정별 작업 실행 화면에서 **각 단계별 진행 상태 시각화**  
 - 직원별 담당 공정 확인 및 **스케줄 자동 생성 기능** 포함  
 - 자재 소요량 계산 기반의 구매 요청 연동 (기초 로직 구현)
 
 #### ✅ 2. 공정 흐름 및 라우팅 관리
 - 당화 → 여과 → 끓임 → 냉각 → 발효 → 숙성 등 **맥주 양조 흐름 시스템화**
 - 공정별 작업 상태를 **실시간 카드 뷰 + 타이머**로 확인 가능  
 - 제품별 라우팅 자동 구성, 워크플로우 오류 감지로 **불량률 예방 시도**
 
 #### ✅ 3. 생산 실적 모니터링 및 분석
 - 월별 계획 대비 실적, 공정별 효율성 등 **대시보드 제공**  
 - KPI 기반 생산성/불량률 시각화 및 히트맵 UI로 이상 흐름 인지  
 - **공정별 랭킹**, 실시간 상태 표시 등으로 관리자 대응 효율 향상
 
 
 💡 *본 프로젝트는 실제 ERP 구조를 바탕으로 프론트/백엔드 연동과 UX 시뮬레이션을 중심으로 구현되었으며,  
 일부 보안 및 인증 기능은 개발 범위 외로 처리되었습니다.*
 
 - [📒 팀 노션](https://www.notion.so/ohgiraffers/QualityCore-7920de328e0346fc91ac1beb96191344)
 - [🏳️‍🌈 Figma](https://www.figma.com/design/KaKLszFW3IRpA7HM89VoiI/Qulity_Core?node-id=0-1&p=f&t=5U3P5uWp6YptJbit-0)

 
 ---

 
 <a name = "프로젝트-일정"></a>
 ## 📌 프로젝트 일정
<img width="100%" alt="TimeLine" src="https://github.com/user-attachments/assets/7da436a3-9163-476d-b333-8682987ff97e" />

---


<a name = "기술-스택"></a>
## 📌 기술 스택


### 🧠 Backend
| 구분 | 사용 기술 |
|------|-----------|
| **프레임워크 & 언어** | Java 17, Spring Boot |
| **ORM & DB** | Spring Data JPA, MyBatis, Hibernate, OracleDB |
| **쿼리** | QueryDSL |
| **템플릿 엔진** | Thymeleaf |
| **보안 & 인증** | JWT 로그인 기능 구현 예정 (팀원 이탈로 미완성) |
| **API 문서화** | SpringDoc OpenAPI (Swagger) |
| **모델 매핑** | ModelMapper |
| **웹소켓** | Spring Boot WebSocket (STOMP) |
| **이메일 전송** | JavaMailSender (SMTP, Gmail 연동) |
| **PDF/문서 처리** | Apache POI (Excel 처리) |
| **파일 업로드** | Cloudinary (cloudinary-core, cloudinary-http44) |
| **입력 검증** | Spring Validation (JSR-380) |
| **로그 & AOP** | Lombok (@Slf4j), Spring AOP |
| **배포 환경** | 로컬 개발 환경 (Oracle DB 기준), Railway 배포 고려 중|
| **테스트** | JUnit, MyBatis Test |
| **기타 도구** | DevTools, ojdbc8 / ojdbc11 드라이버 |

<br/>
<br/>

## 🎨 Frontend 기술 스택

| 구분 | 기술 |
|------|------|
| **프레임워크** | React |
| **기본 기술** | HTML5, CSS3, JavaScript |
| **라우팅** | React Router DOM |
| **상태 관리** | useState, useEffect, useMemo, useCallback |
| **HTTP 통신** | fetch API, axios |
| **UI 구성** | MUI, Lucide-React, Chart.js |
| **모달 처리** | react-modal |
| **애니메이션** | Lottie-React, JS-Confetti |
| **이미지 처리** | Lazy Loading, Canvas API (라벨 그리기) |
| **파일 업로드** | Blob 변환 후 Multipart 전송 |
| **마크다운 렌더링** | marked |
| **사용자 인증** | Context API (useAuth) |
| **스타일링** | CSS Modules, Flexbox 기반 레이아웃 |
| **반응형** | Media Query (모바일 대응) |
| **마이크로 UX** | Gradient, Hover, Shadow, Transition 효과 |
| **기타** | 단계별 플로우 구성, 커스텀 캘린더, 공정 카드 뷰 |

<br/>
<br/>


## ☁️ 파일 저장 & 데이터 백업

| 구분 | 기술 |
|------|------|
| **DB 백업 & 복제** | Oracle XE (로컬 환경, 별도 복제 구성 없음) |

“현재는 Oracle XE 기반 로컬 저장소에서 테스트 진행했으며, 추후 클라우드 파일 저장소 및 DB 이중화 구성을 고려 중입니다.”

<br/>

---

<br/>
<br/>


<a name="기능-설명"></a>
## 📌 기능 설명

<br/>


---

<br/>

### 생산계획

<br/>

---

<br/>

### 작업업지시

<br/>

---

<br/>

### 🍺 생산공정 관리

<br/>

#### 작업지시번호 및 자재정보 , PDF 파일

<img src="https://github.com/user-attachments/assets/ba9bbe32-cf1a-4e52-92c0-8978448d3e1b" width="600"/>

<br/>

[LOT2025032501_작업지시서.pdf](https://github.com/user-attachments/files/19444867/LOT2025032501_.pdf)

#### 📄 작업지시서 및 자재정보 (PDF 출력)
작업지시서와 자재정보는 공정 진행 중 해당 지시서대로 공정이 정확히 수행되는지 검토할 수 있도록 구성되어 있습니다.
각 공정에 필요한 원재료, 투입량, 작업지시번호를 실시간으로 확인·비교할 수 있어, 현장 작업자나 관리자 모두 손쉽게 대응이 가능합니다

※ 참고: 실제로는 공정별 소요시간이 상이하나, 데모 및 검증 목적상 **모든 공정의 타이머를 5초로 통일**하여 구현하였습니다. <br/>
이를 통해 전체 흐름을 빠르게 확인할 수 있도록 구성했습니다.

<hr/>
<br/>


#### 분쇄 공정

![분쇄공정](https://github.com/user-attachments/assets/251f617c-51ba-4234-957f-4fc66d4690b7) <br/>

#### ✅ 분쇄 공정

작업지시서(LOT 번호)가 생성되면, 분쇄 공정 화면에서 해당 작업지시 번호를 선택할 수 있습니다.
선택한 작업지시 정보에 따라 원재료(보리, 밀, 쌀 등)와 맥아 종류, 투입량이 자동으로 불러와지며,
해당 원재료에 맞는 **분쇄 간격(mm), 분쇄 속도(RPM)** 도 자동으로 설정됩니다.

작업자는 이 데이터를 기반으로 실제 분쇄 작업을 수행합니다.
"등록하기" 버튼을 통해 공정 시작 시점을 기록하면, 설정된 시간에 맞춰 타이머가 작동되며 공정이 진행됩니다.
타이머가 종료되면, 공정 완료 모달이 나타나며 "다음 공정 이동" 버튼을 통해 후속 공정으로 넘어갑니다.

<hr/>
<br/>

#### 당화 공정

![당화공정](https://github.com/user-attachments/assets/acec4202-3b6b-4734-8825-3ba1b0a5b82c)

#### 🌾 당화공정 
당화공정은 **분쇄공정에서 처리된 원재료(주원료 + 맥아)** 와 물을 혼합하여 당화 반응을 유도하는 단계입니다.

해당 화면에서는 작업지시 ID를 기준으로 이전 공정에서 설정된 분쇄 원료의 총 투입량을 곡물 기준값 1로 환산하고,
작업지시서에서 지정된 물 투입량을 자동으로 불러와 곡물 대비 물의 비율을 실시간으로 계산하여 확인할 수 있습니다.

이를 통해 작업자는 원료 대비 물 비율이 적정한지 직관적으로 확인할 수 있으며,
혼합이 완료된 시점에서는 설비의 측정값을 기반으로 pH 값을 수동 입력하도록 되어 있습니다.

<hr/>
<br/>

#### 여과 공정

![여과공정](https://github.com/user-attachments/assets/766b65ad-0dd6-4d37-a7b6-13911e59d77d)

#### 🧪 여과공정
여과공정은 당화공정에서 생성된 워트를 필터링하여 맑은 맥즙을 분리하는 단계입니다.

해당 화면에서는 작업지시 ID를 기준으로 당화 시 설정된 워트 총량과 **곡물 흡수량(1.4L/kg)** 을 자동 연산하여, 회수 가능한 워트량을 계산합니다.
이후, 필터링 과정에서 발생하는 손실량은 **회수된 워트량의 5%** 로 자동 산출되며, 최종 회수 워트량은 손실량을 반영하여 실시간으로 업데이트됩니다.

이를 통해 작업자는 **여과 후 실제 수율을 확인** 하고, **손실량의 비정상 여부**를 직관적으로 판단할 수 있습니다.

<hr/>
<br/>

#### 끓임 공정

![끓임공정gif](https://github.com/user-attachments/assets/cb385815-b42e-4291-8ce5-40462a2a9556)

#### 🔥 끓임공정
끓임공정은 여과된 맥즙에 홉을 투입한 후, 고온에서 끓이는 단계입니다.

공정은 설정된 온도에 도달하면 모달창이 표시되며, 사용자가 확인하면 끓임공정이 시작됩니다.<br/>
온도 변화는 타이머 기반 구조로 설계되어, 끓임 설비의 온도 변화를 실시간으로 시각화할 수 있도록 구성했습니다.<br/>

초기 워트량은 **여과공정에서 최종 회수된 워트량을 기준** 으로 자동 설정되며,<br/>
홉 투입 정보는 **작업지시서에 등록된 자재 정보를 기반** 으로 자동 불러옵니다.<br/>
첫 번째와 두 번째 홉의 투입량이 자동으로 입력되어, 작업자는 이를  확인할 수 있습니다.<br/>

끓임 종료 시, **끓임 손실량(초기 워트량의 5%)이 자동 계산되어 표시되며** ,<br/>
최종 끓임 후 워트량은 초기 워트량 - 끓임 손실량으로 실시간 산출되어 업데이트됩니다.<br/>

이 과정을 통해 작업자는 홉 투입 시점과 수율 변화 상황을 직관적으로 파악할 수 있습니다.

<hr/>
<br/>

#### 냉각 공정

![냉각공정gif](https://github.com/user-attachments/assets/717c7b8e-c0c4-42f0-ab27-ca0c9a4c3e17)

#### ❄️ 냉각공정
냉각공정은 끓임공정에서 가열된 맥즙을 설정된 적정 온도까지 빠르게 식히는 단계입니다.<br/>
온도가 설정값에 도달하면 모달창이 표시되며, 사용자가 확인하면 냉각공정이 시작됩니다.<br/>

냉각수의 온도는 기본값으로 설정되어 있으며, 냉각 설비에 따라 온도 변화가 가능하도록 타이머 기반 구조로 설계되었습니다.<br/>
냉각공정 또한 끓임공정과 마찬가지로 타이머 기반으로 구성되어, 냉각 설비의 온도 변화를 실시간으로 시각화할 수 있도록 구현되었습니다.<br/>

<hr/>
<br/>


#### 발효 공정

![발효공정](https://github.com/user-attachments/assets/af637a06-ecb7-40a0-b62a-b7955622eaf0)

#### 🌾 발효공정
발효공정은 맥즙에 효모를 투입하여 알코올과 향을 생성하는 핵심 단계입니다.

설정된 온도에 도달하면 모달창이 표시되며, 사용자가 확인하면 발효공정이 시작됩니다. <br/>
이때 작업지시에 등록된 자재 정보를 기반으로 효모의 종류와 투입량이 자동으로 입력됩니다. <br/>
공정이 완료되면 최종 당도를 입력하게 되며, 이 값은 이후 공정의 품질 기준으로 활용됩니다. <br/>

<hr/>
<br/>

#### 숙성 공정

![숙성공정](https://github.com/user-attachments/assets/0d1050af-08d4-417d-a8f6-89c086312ff4)

#### 🧊 숙성공정
숙성공정은 발효가 완료된 맥주를 일정 온도와 압력에서 안정화시키며,<br/>
맛, 향, 탄산 밸런스를 조정하는 최종 조율 단계입니다.<br/>

설비의 기본값에 따라 시작 온도, 현재 온도, 압력, CO₂ 농도, 용존 산소량이 자동 설정됩니다. <br/>

<hr/>
<br/>

#### 숙성 후 여과 공정

![숙성 후 여과 공정](https://github.com/user-attachments/assets/f24b5bc5-7be3-41eb-b65a-58d9ade2c7b7)

#### 🧊 숙성 후 여과 공정
숙성된 맥주를 최종 여과하여 맑고 깨끗한 품질로 완성하는 공정입니다.

이 과정을 통해 맥주의 청량한 외관과 깔끔한 맛을 유지할 수 있습니다. <br/>

여과가 완료되면,탁도(탁한 정도)를 측정하여 품질을 확인하고 <br/>
기준에 적합한 경우, 다음 공정으로 이동됩니다.

<hr/>
<br/>

#### 탄산조정 공정

![탄산 조정 공정](https://github.com/user-attachments/assets/086959d9-5154-413a-9abc-2fa986d22af5)

#### 🫧 탄산조정 공정
완성된 맥주에 탄산을 주입하여 적절한 탄산감을 부여하는 공정입니다.

이 과정을 통해 맥주의 풍미를 더욱 살리고, 톡 쏘는 청량감을 구현할 수 있습니다.<br/>

CO₂ 농도, 탄산 공정 온도, 공정 중 압력은 기본값에 따라 자동 설정되며,
설정값은 제조 기준에 맞춰 최적화되어 있습니다.<br/>
탄산 조정이 완료되면 마지막 공정으로 전환됩니다.

<br/>

#### 패키징 및 출하

![패키징 및 출하](https://github.com/user-attachments/assets/1f26e62f-0be7-4df2-96ca-ee5573c8e2f5)


#### 📦 패키징 및 출하 공정

패키징 및 출하 공정은 제품 완성 후 마지막 단계로, 충전부터 라벨링, 포장, 출하 등록까지 진행됩니다.

작업지시가 로드되면 충전 상태, 세척 및 살균, 밀봉, 라벨링, 포장 상태를 선택할 수 있으며, 각 항목은 기본적으로 '양호'로 설정됩니다.

제품명과 출하일은 작업지시서에서 자동 연동되며, 수량과 목적지를 입력하면 출하 정보가 등록됩니다.

공정 중 산소 농도나 밀봉 압력 등은 기준에 따라 상태가 자동 표시되어 작업자는 확인만 하면 됩니다.

출하 대기 상태가 완료되면, 작업지시 관리로 공정 상태가 넘어가며 해당 작업의 전체 공정 완료 여부를 한눈에 확인할 수 있습니다.

<br/>

---

<br/>

### Routing 관리

<br/>

---

<br/>

### 생산실적 관리

<br>

---


<br/>
<br/>


---

<a name="논리--물리-데이터-모델링"></a>
## 📌 논리 데이터 모델링 ( 총 32개 테이블 )
<img width="100%" alt="생산 ERP 모델링" src="https://github.com/user-attachments/assets/0e366ad1-b38f-4663-a446-1cd0cfef48fa" />

<br/>
<br/>

---

