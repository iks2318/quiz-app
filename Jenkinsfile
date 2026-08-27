pipeline {

    agent any

    environment {

        // ============================================================
        // JAVA
        // ============================================================

        JAVA_HOME = 'C:/Program Files/Java/jdk-17.0.2'

        // ============================================================
        // BACKEND
        // ============================================================

        APP_JAR = 'target/quizapp.jar'
        BACKEND_PORT = '8090'
        BACKEND_URL = 'http://localhost:8090/api/user/quizzes'
    }

    stages {

        // ============================================================
        // 1. CHECKOUT
        // ============================================================

        stage('Checkout') {
            steps {

                echo '=========================================='
                echo 'CHECKING OUT QUIZ APP'
                echo '=========================================='

                git branch: 'main',
                    url: 'https://github.com/iks2318/quiz-app.git'
            }
        }


        // ============================================================
        // 2. CHECK WORKSPACE
        // ============================================================

        stage('Check Workspace') {
            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo CHECKING JENKINS WORKSPACE
                    echo ==========================================

                    echo Workspace:
                    echo %WORKSPACE%

                    echo.
                    echo Workspace contents:
                    dir

                    echo.
                    echo Checking pom.xml...

                    if not exist "pom.xml" (
                        echo ERROR: pom.xml not found.
                        exit /b 1
                    )

                    echo pom.xml found successfully.

                    echo.
                    echo Checking src directory...

                    if not exist "src" (
                        echo ERROR: src directory not found.
                        exit /b 1
                    )

                    echo src directory found successfully.

                    echo.
                    echo Workspace check completed successfully.
                '''
            }
        }


        // ============================================================
        // 3. BUILD BACKEND
        // ============================================================

        stage('Build Backend') {
            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BUILDING QUIZAPP BACKEND
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"
                    set "PATH=%JAVA_HOME%\\bin;%PATH%"

                    echo.
                    echo ==========================================
                    echo JAVA VERSION
                    echo ==========================================

                    java -version

                    if errorlevel 1 (
                        echo ERROR: Java is not working.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN VERSION
                    echo ==========================================

                    mvn -version

                    if errorlevel 1 (
                        echo ERROR: Maven is not working.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo STARTING MAVEN BUILD
                    echo ==========================================

                    call mvn -B clean package -DskipTests

                    if errorlevel 1 (
                        echo.
                        echo ==========================================
                        echo MAVEN BUILD FAILED
                        echo ==========================================
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo MAVEN BUILD SUCCESSFUL
                    echo ==========================================

                    echo.
                    echo TARGET DIRECTORY:
                    dir target

                    echo.
                    echo Checking quizapp.jar...

                    if not exist "target\\quizapp.jar" (
                        echo ERROR: target\\quizapp.jar was not generated.
                        exit /b 1
                    )

                    echo.
                    echo quizapp.jar found successfully.
                '''
            }
        }


        // ============================================================
        // 4. TEST
        // ============================================================

        stage('Test') {
            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo RUNNING TESTS
                    echo ==========================================

                    call mvn -B test

                    if errorlevel 1 (
                        echo ERROR: Tests failed.
                        exit /b 1
                    )

                    echo.
                    echo ==========================================
                    echo TESTS COMPLETED SUCCESSFULLY
                    echo ==========================================
                '''
            }
        }


        // ============================================================
        // 5. ARCHIVE JAR
        // ============================================================

        stage('Archive JAR') {
            steps {

                archiveArtifacts artifacts: 'target/*.jar',
                    fingerprint: true
            }
        }


        // ============================================================
        // 6. STOP OLD BACKEND
        // ============================================================

        stage('Stop Old Backend') {
            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo CHECKING PORT 8090
                    echo ==========================================

                    netstat -ano | findstr LISTENING | findstr ":8090"

                    echo.

                    for /f "tokens=5" %%a in ('netstat -ano ^| findstr LISTENING ^| findstr ":8090"') do (

                        echo Found process %%a using port 8090.

                        echo Stopping process %%a...

                        taskkill /F /PID %%a >nul 2>&1
                    )

                    echo.
                    echo Waiting for port 8090 to become free...

                    ping -n 4 127.0.0.1 >nul

                    echo.
                    echo Port 8090 is ready.
                '''
            }
        }


        // ============================================================
        // 7. START BACKEND
        // ============================================================

        stage('Start Backend') {
            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo STARTING QUIZAPP BACKEND
                    echo ==========================================

                    set "JAVA_HOME=C:\\Program Files\\Java\\jdk-17.0.2"

                    if exist "%WORKSPACE%\\backend.log" (
                        del /F /Q "%WORKSPACE%\\backend.log"
                    )

                    if exist "%WORKSPACE%\\backend-err.log" (
                        del /F /Q "%WORKSPACE%\\backend-err.log"
                    )

                    echo.
                    echo Starting Spring Boot application...

                    echo.
                    echo JAR:
                    echo %WORKSPACE%\\target\\quizapp.jar

                    echo.
                    echo PORT:
                    echo 8090

                    powershell -NoProfile -Command "$env:JENKINS_NODE_COOKIE='dontKillMe'; Start-Process -FilePath '%JAVA_HOME%\\bin\\java.exe' -ArgumentList '-jar','%WORKSPACE%\\target\\quizapp.jar','--server.port=8090' -RedirectStandardOutput '%WORKSPACE%\\backend.log' -RedirectStandardError '%WORKSPACE%\\backend-err.log' -WindowStyle Hidden"

                    echo.
                    echo ==========================================
                    echo BACKEND START COMMAND EXECUTED
                    echo ==========================================

                    echo.
                    echo Waiting for Spring Boot...

                    ping -n 11 127.0.0.1 >nul

                    echo.
                    echo ==========================================
                    echo PORT 8090 STATUS
                    echo ==========================================

                    netstat -ano | findstr LISTENING | findstr ":8090"

                    echo.
                    echo ==========================================
                    echo BACKEND LOG
                    echo ==========================================

                    if exist "%WORKSPACE%\\backend.log" (
                        powershell -NoProfile -Command "Get-Content '%WORKSPACE%\\backend.log' -Tail 100"
                    ) else (
                        echo backend.log not found.
                    )

                    if exist "%WORKSPACE%\\backend-err.log" (
                        echo.
                        echo ==========================================
                        echo BACKEND ERROR LOG
                        echo ==========================================

                        powershell -NoProfile -Command "Get-Content '%WORKSPACE%\\backend-err.log' -Tail 100"
                    )
                '''
            }
        }


        // ============================================================
        // 8. BACKEND HEALTH CHECK
        // ============================================================

        stage('Backend Health Check') {
            steps {

                bat '''
                    @echo off

                    echo ==========================================
                    echo BACKEND HEALTH CHECK
                    echo ==========================================

                    echo Backend URL:
                    echo http://localhost:8090

                    echo.
                    echo Backend API:
                    echo http://localhost:8090/api/user/quizzes

                    echo.
                    echo ==========================================
                    echo WAITING FOR BACKEND
                    echo ==========================================

                    set RETRIES=30

                    :CHECK_BACKEND

                    echo.
                    echo Attempts remaining: %RETRIES%

                    netstat -ano | findstr LISTENING | findstr ":8090" >nul

                    if not errorlevel 1 (

                        echo.
                        echo ==========================================
                        echo BACKEND IS RUNNING
                        echo ==========================================

                        echo Port 8090 is listening.

                        echo.
                        echo Testing API...

                        curl -s -o nul -w "HTTP Status: %%{http_code}" "http://localhost:8090/api/user/quizzes"

                        echo.
                        echo.
                        echo Backend application is running successfully.

                        exit /b 0
                    )

                    echo Backend is not ready yet.

                    set /a RETRIES-=1

                    if %RETRIES% LEQ 0 (

                        echo.
                        echo ==========================================
                        echo BACKEND FAILED TO START
                        echo ==========================================

                        echo.
                        echo PORT STATUS:

                        netstat -ano | findstr ":8090"

                        echo.
                        echo ==========================================
                        echo BACKEND LOG
                        echo ==========================================

                        if exist "%WORKSPACE%\\backend.log" (
                            type "%WORKSPACE%\\backend.log"
                        ) else (
                            echo backend.log not found.
                        )

                        echo.
                        echo ==========================================
                        echo BACKEND ERROR LOG
                        echo ==========================================

                        if exist "%WORKSPACE%\\backend-err.log" (
                            type "%WORKSPACE%\\backend-err.log"
                        ) else (
                            echo backend-err.log not found.
                        )

                        exit /b 1
                    )

                    echo Waiting 2 seconds...

                    ping -n 3 127.0.0.1 >nul

                    goto CHECK_BACKEND
                '''
            }
        }
    }


    // ============================================================
    // POST ACTIONS
    // ============================================================

    post {

        success {

            echo '=========================================='
            echo 'QUIZAPP DEPLOYMENT SUCCESSFUL'
            echo '=========================================='

            echo 'Backend:'
            echo 'http://localhost:8090'

            echo 'Quiz API:'
            echo 'http://localhost:8090/api/user/quizzes'

            echo '=========================================='
        }

        failure {

            echo '=========================================='
            echo 'QUIZAPP DEPLOYMENT FAILED'
            echo '=========================================='

            echo 'Check the failed stage.'
            echo 'Check backend.log.'
            echo 'Check backend-err.log.'

            echo '=========================================='
        }

        always {

            echo '=========================================='
            echo 'JENKINS PIPELINE COMPLETED'
            echo '=========================================='
        }
    }
}