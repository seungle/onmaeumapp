## onmaeumapp

## 프로젝트 소개
**감정 기록 기반 정신 건강 관리 앱 ‘온마음(Onmaeum)’**은 감정을 표현하기 어려운 사람들을 위해, 감정을 기록하고 시각화하며 명상 콘텐츠를 통해 정서적 안정과 자기 돌봄을 유도하는 모바일 애플리케이션입니다.

## 개발 기간
2024.11 ~ 2025.06 (8개월)

-요구사항 정의 및 UI 기획

-앱 구조 설계 및 기술 선정

-MVP 기능 구현 및 테스트

-UI/UX 개선 및 통계 시각화



## 개발 환경
--Language--: Kotlin

--Framework--: Android Jetpack, Jetpack Compose

--IDE--: Android Studio

--Build System--: Gradle

--Architecture--: MVVM

## 기술 스택
--Local DB--: Room Database

--설정 저장--: Jetpack DataStore (Preferences)

--UI--: Jetpack Compose + Material Design 3

--DI--: Hilt

--비동기 처리--: Kotlin Coroutines

--알림/백그라운드--: WorkManager

--통계 시각화--: MPAndroidChart

--NLP 감정 분석--: KoBERT 기반 모델 설계 (추가 연동 고려)

--데이터 변환--: Gson


## 주요 기능
1. 감정 기록
감정 선택 (행복/슬픔/분노 등)

스트레스 레벨 기록

자유 일기 작성

2. 명상 콘텐츠
감정 분석 기반 맞춤 명상 추천

오디오 기반 콘텐츠 재생

명상 수행 기록 저장

3. 통계 및 분석
감정 변화 그래프

스트레스 레벨 추이 시각화

명상 완료율 통계 차트

4. 캘린더 & 타임라인
감정 기록을 달력에 시각화

하루 감정 흐름 타임라인 제공

5. 알림 기능
감정 기록 리마인더

명상 습관 형성 알림

WorkManager를 이용한 백그라운드 트리거

6. 데이터 관리
백업/복원 기능

향후 Firebase 연동 가능 구조 고려

## 앱 아키텍처
plaintext
복사
편집
[User UI (Jetpack Compose)]
       ↓
[ViewModel (MVVM 구조)]
       ↓
[Room DB / DataStore / Repository]
       ↓
[로컬 데이터 저장 및 로직 처리]
폴더 구조 요약:

yaml
복사
편집
├── data/          : DB, 모델 클래스
├── ui/            : Compose 기반 화면
├── viewmodel/     : ViewModel 모듈
├── util/          : 날짜 처리 등 유틸
├── notification/  : 알림 기능
├── nlp/           : 감정 분석 관련
└── backup/        : 백업/복원 로직
## API (내부 로직 기준)
감정 기록 저장
EmotionEntry Entity에 감정, 스트레스 수치, 일기 등을 저장

명상 추천 알고리즘
감정 키워드 기반 추천 로직 구성

향후 TensorFlow Lite + KoBERT 연동 계획

통계 데이터 가공
날짜별 감정 점수 평균

스트레스 변화 추이 분석

## 특별한 점
개인 감정 흐름 시각화 → 자기 인식 강화

정서 기반 맞춤형 명상 추천 → 단순 콘텐츠 소비 이상

작은 목표 형성 (알림 + 기록) → 습관화 설계 고려

모든 기능을 로컬 DB 기반으로 안정적으로 구현

## 최종 목표
사용자 감정 데이터를 기반으로 한 맞춤 솔루션 제공

감정 표현이 어려운 사용자에게 **"내 감정 보기 좋은 도구"**가 되는 앱

포트폴리오 및 논문 제출용으로 활용 (UX와 기능성 중심)
