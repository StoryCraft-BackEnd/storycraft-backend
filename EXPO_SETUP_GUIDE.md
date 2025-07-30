# Expo Google OAuth2 설정 가이드

## 1. Expo 계정 정보 확인

### 1.1 Expo 사용자명 확인
```bash
# Expo CLI로 로그인 상태 확인
npx expo whoami

# 또는 Expo 웹사이트에서 확인
# https://expo.dev/accounts
```

### 1.2 앱 슬러그 확인
```json
// app.json 또는 app.config.js에서 확인
{
  "expo": {
    "name": "StoryCraft",
    "slug": "storycraft-app",  // 이 값이 앱 슬러그
    "scheme": "storycraft"
  }
}
```

## 2. Google Cloud Console 설정

### 2.1 프로젝트 생성
1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. **새 프로젝트** 생성: `StoryCraft`

### 2.2 Google+ API 활성화
1. **API 및 서비스** > **라이브러리**
2. **Google+ API** 검색 후 **사용** 클릭

### 2.3 OAuth2 클라이언트 생성
1. **API 및 서비스** > **사용자 인증 정보**
2. **사용자 인증 정보 만들기** > **OAuth 2.0 클라이언트 ID**
3. **애플리케이션 유형**: **웹 애플리케이션**
4. **이름**: `StoryCraft Expo App`

### 2.4 승인된 리디렉션 URI 설정
```
https://auth.expo.io/@your-expo-username/storycraft-app
```

**예시:**
- 사용자명: `john_doe`
- 앱 슬러그: `storycraft-app`
- 리디렉션 URI: `https://auth.expo.io/@john_doe/storycraft-app`

### 2.5 클라이언트 정보 복사
- **클라이언트 ID**: `123456789-abcdef.apps.googleusercontent.com`
- **클라이언트 보안 비밀번호**: `GOCSPX-...`

## 3. Expo 앱 설정

### 3.1 필요한 패키지 설치
```bash
npx expo install expo-auth-session expo-crypto expo-web-browser
```

### 3.2 app.json 설정
```json
{
  "expo": {
    "name": "StoryCraft",
    "slug": "storycraft-app",
    "scheme": "storycraft",
    "web": {
      "bundler": "metro"
    },
    "plugins": [
      [
        "expo-auth-session",
        {
          "scheme": "storycraft"
        }
      ]
    ]
  }
}
```

### 3.3 Google Sign-In 컴포넌트 구현
```javascript
import React from 'react';
import { Button, Alert } from 'react-native';
import * as AuthSession from 'expo-auth-session';
import * as WebBrowser from 'expo-web-browser';

WebBrowser.maybeCompleteAuthSession();

const GoogleSignIn = () => {
  const [request, response, promptAsync] = AuthSession.useAuthRequest(
    {
      clientId: 'YOUR_CLIENT_ID.apps.googleusercontent.com', // Google Cloud Console에서 복사
      scopes: ['openid', 'profile', 'email'],
      redirectUri: AuthSession.makeRedirectUri({
        scheme: 'storycraft'
      }),
      responseType: AuthSession.ResponseType.IdToken,
    },
    {
      authorizationEndpoint: 'https://accounts.google.com/o/oauth2/v2/auth',
      tokenEndpoint: 'https://oauth2.googleapis.com/token',
    }
  );

  React.useEffect(() => {
    if (response?.type === 'success') {
      const { id_token } = response.params;
      console.log('ID Token:', id_token); // 테스트용 로그
      sendIdTokenToServer(id_token);
    }
  }, [response]);

  const signIn = async () => {
    try {
      await promptAsync();
    } catch (error) {
      Alert.alert('로그인 실패', error.message);
    }
  };

  const sendIdTokenToServer = async (idToken) => {
    try {
      const response = await fetch('http://localhost:8080/auth/oauth2/google/android', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          idToken: idToken
        })
      });

      const data = await response.json();
      
      if (data.status === 200) {
        Alert.alert('로그인 성공', '구글 로그인이 완료되었습니다.');
        console.log('JWT Tokens:', data.data);
      } else {
        Alert.alert('로그인 실패', data.message);
      }
    } catch (error) {
      Alert.alert('서버 오류', error.message);
    }
  };

  return (
    <Button
      title="Google로 로그인"
      onPress={signIn}
      disabled={!request}
    />
  );
};

export default GoogleSignIn;
```

## 4. 테스트 방법

### 4.1 Expo 개발 서버 실행
```bash
npx expo start
```

### 4.2 구글 로그인 테스트
1. Expo 앱에서 **Google로 로그인** 버튼 클릭
2. 구글 로그인 페이지에서 인증
3. 콘솔에서 ID 토큰 확인

### 4.3 Swagger에서 테스트
1. Expo 앱에서 얻은 ID 토큰 복사
2. `http://localhost:8080/swagger-api-docs` 접속
3. **Google OAuth2** > **구글 로그인** API 테스트
4. ID 토큰을 Request Body에 입력 후 Execute

## 5. 문제 해결

### 5.1 일반적인 오류
- **리디렉션 URI 불일치**: Expo 사용자명과 앱 슬러그 확인
- **클라이언트 ID 오류**: Google Cloud Console에서 정확히 복사
- **네트워크 오류**: 서버 URL 확인 (localhost:8080)

### 5.2 디버깅
- Expo 개발자 도구에서 네트워크 요청 확인
- 서버 로그에서 ID 토큰 검증 과정 확인
- Google Cloud Console에서 OAuth2 동의 화면 설정 확인