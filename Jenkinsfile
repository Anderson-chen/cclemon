pipeline {


  agent none
  
  environment {
    TZ = 'Asia/Taipei'
  }

  stages {

    stage('Clean Workspace') {
      steps {
        deleteDir()
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

    stage('Build Docker Image') {
      steps {
        dir('cclemon-auth') {
          script {
            def imageName = "cclemon-auth"
            def imageTag = "latest"

            sh "docker build -t ${imageName}:${imageTag} ."
            // 如果需要推送到遠端 registry，取消下面註解
            // sh "docker push ${imageName}:${imageTag}"
          }
        }
      }
    }
  }
}
