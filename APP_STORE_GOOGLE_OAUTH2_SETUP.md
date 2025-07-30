# 앱스토어 배포용 Google OAuth2 설정 가이드

## 1. 현재 상황 분석

### 1.1 개발 vs 배포 차이점
| 구분 | 개발 (Expo Go) | 배포 (앱스토어) |
|------|----------------|-----------------|
| **앱 타입** | Expo Go 앱 | 네이티브 앱 |
| **OAuth2 타입** | 웹 애플리케이션 | Android + iOS |
| **리디렉션 URI** | auth.expo.io | 네이티브 앱 스킴 |
| **테스트** | Expo Go | 실제 앱 |

### 1.2 필요한 설정
1. **개발용**: 웹 애플리케이션 클라이언트 (Expo Go)
2. **배포용**: Android 클라이언트 (Google Play)
3. **배포용**: iOS 클라이언트 (App Store)

## 2. Google Cloud Console 설정

### 2.1 개발용 클라이언트 (웹 애플리케이션)
```
애플리케이션 유형: 웹 애플리케이션
이름: StoryCraft Expo Development
승인된 리디렉션 URI: https://auth.expo.io/@username/StoryCraft
```

### 2.2 배포용 Android 클라이언트
```
애플리케이션 유형: Android
패키지명: com.yourcompany.storycraft (EAS Build에서 생성)
SHA-1 인증서 지문: (릴리즈 키에서 생성)
```

### 2.3 배포용 iOS 클라이언트
```
애플리케이션 유형: iOS
번들 ID: com.yourcompany.storycraft (EAS Build에서 생성)
```

## 3. Expo 앱 설정 수정

### 3.1 app.json 수정
```json
{
  "expo": {
    "name": "StoryCraft",
    "slug": "StoryCraft",
    "scheme": "storycraft", // Google OAuth2용으로 변경
    "version": "1.0.0",
    "orientation": "portrait",
    "icon": "./assets/images/character/sleep.png",
    "userInterfaceStyle": "automatic",
    "newArchEnabled": true,
    "ios": {
      "supportsTablet": true,
      "bundleIdentifier": "com.yourcompany.storycraft"
    },
    "android": {
      "adaptiveIcon": {
        "foregroundImage": "./assets/images/character/sleep.png",
        "backgroundColor": "#ffffff"
      },
      "package": "com.yourcompany.storycraft"
    },
    "web": {
      "bundler": "metro",
      "output": "static",
      "favicon": "./assets/images/character/sleep.png"
    },
    "plugins": [
      "expo-router",
      [
        "expo-splash-screen",
        {
          "image": "./assets/images/character/sleep.png",
          "imageWidth": 200,
          "resizeMode": "contain",
          "backgroundColor": "#ffffff"
        }
      ],
      [
        "expo-auth-session",
        {
          "scheme": "storycraft"
        }
      ]
    ],
    "experiments": {
      "typedRoutes": true
    },
    "extra": {
      "router": {
        "origin": false
      },
      "eas": {
        "projectId": "your-project-id"
      }
    },
    "metro": {
      "maxWorkers": 2,
      "resetCache": true
    }
  }
}
```

### 3.2 Google Sign-In 컴포넌트 수정
```javascript
import React from 'react';
import { Button, Alert, Platform } from 'react-native';
import * as AuthSession from 'expo-auth-session';
import * as WebBrowser from 'expo-web-browser';

WebBrowser.maybeCompleteAuthSession();

const GoogleSignIn = () => {
  // 개발/배포 환경에 따른 클라이언트 ID 선택
  const getClientId = () => {
    if (__DEV__) {
      // 개발 환경: 웹 애플리케이션 클라이언트 ID
      return 'DEVELOPMENT_CLIENT_ID.apps.googleusercontent.com';
    } else {
      // 배포 환경: 플랫폼별 클라이언트 ID
      if (Platform.OS === 'ios') {
        return 'IOS_CLIENT_ID.apps.googleusercontent.com';
      } else {
        return 'ANDROID_CLIENT_ID.apps.googleusercontent.com';
      }
    }
  };

  const [request, response, promptAsync] = AuthSession.useAuthRequest(
    {
      clientId: getClientId(),
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
      console.log('ID Token:', id_token);
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
      // 배포 환경에서는 실제 서버 URL 사용
      const serverUrl = __DEV__ 
        ? 'http://localhost:8080' 
        : 'https://your-production-server.com';
        
      const response = await fetch(`${serverUrl}/auth/oauth2/google/android`, {
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

## 4. EAS Build 설정

### 4.1 eas.json 설정
```json
{
  "cli": {
    "version": ">= 5.9.1"
  },
  "build": {
    "development": {
      "developmentClient": true,
      "distribution": "internal"
    },
    "preview": {
      "distribution": "internal"
    },
    "production": {
      "android": {
        "buildType": "apk"
      }
    }
  },
  "submit": {
    "production": {}
  }
}
```

### 4.2 빌드 명령어
```bash
# 개발용 빌드
eas build --profile development --platform android

# 프로덕션 빌드
eas build --profile production --platform android
eas build --profile production --platform ios
```

## 5. 배포 단계

### 5.1 개발 단계
1. **Expo Go**에서 테스트
2. **웹 애플리케이션** 클라이언트 사용
3. **localhost** 서버로 테스트

### 5.2 배포 단계
1. **EAS Build**로 네이티브 앱 빌드
2. **Android/iOS** 클라이언트 사용
3. **프로덕션 서버**로 연결

## 6. 백엔드 설정 (변경 없음)

### 6.1 현재 백엔드 API
```http
POST /auth/oauth2/google/android
{
  "idToken": "google_id_token_here"
}
```

### 6.2 서버 URL 설정
- **개발**: `http://localhost:8080`
- **배포**: `https://your-production-server.com`

## 7. 설정 순서

### 7.1 현재 (개발용)
1. Google Cloud Console에서 **웹 애플리케이션** 클라이언트 생성
2. Expo Go에서 테스트
3. Swagger에서 테스트

### 7.2 배포용 추가 설정
1. Google Cloud Console에서 **Android** 클라이언트 생성
2. Google Cloud Console에서 **iOS** 클라이언트 생성
3. EAS Build로 네이티브 앱 빌드
4. 앱스토어에 배포

## 8. 문제 해결

### 8.1 일반적인 오류
- **개발/배포 환경 구분**: `__DEV__` 플래그 사용
- **클라이언트 ID 불일치**: 환경별 클라이언트 ID 확인
- **서버 URL 오류**: 개발/배포 환경별 URL 설정

### 8.2 디버깅
- 개발: Expo Go에서 네트워크 요청 확인
- 배포: 네이티브 앱에서 네트워크 요청 확인
- 서버 로그에서 ID 토큰 검증 과정 확인 