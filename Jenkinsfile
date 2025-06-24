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
    stage('Checkout') {
      steps {
        checkout scm
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
