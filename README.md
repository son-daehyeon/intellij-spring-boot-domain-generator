# 🚀 Spring Boot Domain Generator

> IntelliJ IDEA용 스프링 부트 도메인 자동 생성 플러그인

## 📋 개요

스프링 부트 프로젝트에서 반복적인 도메인 구조 생성을 자동화하는 IntelliJ IDEA 플러그인입니다.
단 한 번의 키 입력으로 완전한 CRUD 구조를 가진 도메인 패키지를 생성할 수 있습니다.

## ✨ 주요 기능

- **⚡ 원클릭 생성**: `Ctrl + Shift + S`로 즉시 도메인 생성
- **🎯 완전한 CRUD**: Controller, Service, Entity, Repository, DTO, Exception 자동 생성
- **🔗 템플릿 호환**: [Spring Boot Security MySQL JWT Template](https://github.com/son-daehyeon/spring-boot-security-mysql-jwt-template)과 완벽 호환

## 🔧 자동 생성되는 파일들

```
domain/
└── [도메인명]/
    ├── controller/
    │   └── [도메인명]Controller.java
    ├── service/
    │   └── [도메인명]Service.java
    ├── entity/
    │   └── [도메인명]Entity.java
    ├── repository/
    │   └── [도메인명]Repository.java
    ├── dto/
    │   ├── [도메인명]RequestDto.java
    │   └── [도메인명]ResponseDto.java
    └── exception/
        └── [도메인명]NotFoundException.java
```

## 📦 설치 방법

### JetBrains Marketplace에서 설치

1. IntelliJ IDEA → `File` → `Settings` (또는 `Preferences`)
2. `Plugins` → `Marketplace` 탭
3. "Spring Boot Domain Generator" 검색
4. `Install` 클릭
5. IDE 재시작

### 직접 다운로드

[Github Releases](https://github.com/son-daehyeon/intellij-spring-boot-domain-generator/releases)에서 직접 다운로드 가능

## 🚀 사용법

> 이 플러그인을 사용하기 전에 [son-daehyeon/spring-boot-security-mysql-jwt-template](https://github.com/son-daehyeon/spring-boot-security-mysql-jwt-template)를 기반으로 프로젝트를 생성해야 합니다.

1. 프로젝트의 `domain` 폴더로 이동
2. `Ctrl + Shift + S` 단축키 입력 (또는 우클릭 → "New Spring Boot Domain")
3. 도메인 이름 입력
4. 자동으로 완전한 도메인 구조 생성 완료!

## 📸 스크린샷

### 플러그인 사용 화면
<img src="https://plugins.jetbrains.com/files/27960/screenshot_0361bb1e-5bcd-42ad-a37e-881e2adcc62a" width="600">

### 도메인 생성 다이얼로그
<img src="https://plugins.jetbrains.com/files/27960/screenshot_ef2a1ece-3398-4215-a432-49323bc65576" width="600">

### 생성된 파일 구조
<img src="https://plugins.jetbrains.com/files/27960/screenshot_e9ee15a2-5df0-4199-951f-621f65569393" width="400">

## 💻 호환성

| 구분                | 지원 버전             |
|:------------------|:------------------|
| **IntelliJ IDEA** | 2023.1 ~ 2025.1.* |
| **Spring Boot**   | 3.x               |
| **Java**          | 11+               |
