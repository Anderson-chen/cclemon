pipeline {
  agent {
    dockerfile {
      // 你也可以指定 args，通常要掛載 docker.sock 才能用 docker 指令
      args '-v /var/run/docker.sock:/var/run/docker.sock -v $HOME/.gradle:/home/gradle/.gradle'
      // 可指定額外選項，例如：label, additionalBuildArgs 等
    }
  }

  environment {
    TZ = 'Asia/Taipei'
  }

  stages {
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

    stage('Build Docker Image') {
      steps {
        dir('cclemon-auth') {
          script {
            sh "docker-compose up"
          }
        }
      }
    }
  }
}
