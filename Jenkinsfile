pipeline {
    agent any

    environment {
        // GitHub 레포지토리 주소
        GIT_URL = 'https://github.com/StoryCraft-BackEnd/storycraft-backend.git'

        // PostgreSQL 접속 정보 (나중에 데이터베이스 설정이 완료되면 사용)
        SPRING_DATASOURCE_URL = 'jdbc:postgresql://postgres:5432/storycraft'
        SPRING_DATASOURCE_USERNAME = 'user'
        SPRING_DATASOURCE_PASSWORD = 'password'
    }

    stages {
        // 1. Checkout stage: GitHub에서 코드 체크아웃
        stage('Checkout') {
            steps {
                git branch: 'main', url: "${GIT_URL}"
            }
        }

        // 2. Build stage: Gradle을 사용하여 빌드
        stage('Build') {
            steps {
                script {
                    // Gradle 빌드
                    sh './gradlew clean build'
                }
            }
        }

        // 3. Test stage: 테스트 실행
        stage('Test') {
            steps {
                script {
                    // 테스트 실행 (API가 구현되어 있지 않다면, 테스트를 우선 placeholder로 둘 수 있음)
                    // sh './gradlew test'
                    echo '테스트 단계는 나중에 추가됩니다.'
                }
            }
        }

        // 4. (배포 단계는 나중에 추가)
        // stage('Deploy') {
        //     steps {
        //         script {
        //             sh 'docker-compose -f docker/docker-compose.yml up -d'
        //         }
        //     }
        // }

    }

    post {
        success {
            echo '파이프라인 실행 성공!'
        }
        failure {
            echo '파이프라인 실행 실패!'
        }
    }
}
