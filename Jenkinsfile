pipeline {
  agent {
    docker {
      image 'gradle:8.8-jdk21'
      args '-v $HOME/.gradle:/home/gradle/.gradle' // Optional: cache Gradle deps
    }
  }

  environment {
    TZ = 'Asia/Taipei'
  }

  stages {

      stage('Clean Workspace') {
        steps {
          deleteDir() // 清空 Jenkins workspace 目錄
        }
      }


    stage('Checkout') {
      steps {
        checkout scm
        sh 'chmod +x gradlew'
      }
    }

    stage('Build') {
      steps {
        dir('cclemon-auth') {
          sh '../gradlew clean build -x test'
        }
      }
    }

    stage('Archive Jar') {
      steps {
        archiveArtifacts artifacts: 'cclemon-auth/build/libs/*.jar', fingerprint: true
      }
    }
  }
}
