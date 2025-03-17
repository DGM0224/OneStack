pipeline {
    agent any
    
    environment {
        DOCKER_COMPOSE = '/usr/local/bin/docker-compose'
//          KAKAO_CLIENT_ID = credentials('kakao-client-id')
//          KAKAO_REDIRECT_URI = credentials('kakao-redirect-uri')
//          GOOGLE_CLIENT_ID = credentials('google-client-id')
//          GOOGLE_CLIENT_SECRET = credentials('google-client-secret')
//          GOOGLE_REDIRECT_URI = credentials('google-redirect-uri')
        GMAIL_USERNAME = credentials('gmail-username')
        GMAIL_PASSWORD = credentials('gmail-password')
        PORTONE_API_KEY = credentials('portone-api-key')
        PORTONE_API_SECRET = credentials('portone-api-secret')
    }
    
    stages {
        stage('Clean Workspace') {
            steps {
                cleanWs()
            }
        }
        
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Build') {
            steps {
                sh '''
                    # gradlew 파일에 실행 권한 부여
                    chmod +x ./gradlew
                    ./gradlew clean build -x test
                '''
            }
        }

        stage('Check Env Vars') {
                    steps {
                        script {
                            def portoneKey = sh(script: "echo -n \$PORTONE_API_KEY | wc -c", returnStdout: true).trim()
                            def portoneSecret = sh(script: "echo -n \$PORTONE_API_SECRET | wc -c", returnStdout: true).trim()

                            if (portoneKey == "0" || portoneSecret == "0") {
                                error("❌ PORTONE API 환경 변수가 설정되지 않았습니다!")
                            } else {
                                echo "✅ PORTONE API KEY & SECRET이 정상적으로 로드됨 (길이: ${portoneKey}, ${portoneSecret})"
                            }
                        }
                    }
                }
        
        stage('Prepare Environment') {
            steps {
                sh '''
                    sudo mkdir -p /usr/share/nginx/html/images
                    sudo chown -R $(id -u):$(id -g) /usr/share/nginx/html/images
                    sudo chmod -R 755 /usr/share/nginx/html/images
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                sh '''
                    # 이전 컨테이너 중지
                    ${DOCKER_COMPOSE} down --volumes=false || true
                    
                    # 컨테이너 시작
                    ${DOCKER_COMPOSE} up -d --build
                '''
            }
        }
        
        stage('Health Check') {
            steps {
                sh '${DOCKER_COMPOSE} ps'
            }
        }
    }
    
    post {
        failure {
            sh '''
                if command -v ${DOCKER_COMPOSE} &> /dev/null; then
                    ${DOCKER_COMPOSE} logs
                fi
            '''
        }
        always {
            sh 'docker system prune -f --volumes=false'
        }
    }
} 