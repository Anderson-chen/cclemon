pipeline {
  agent {
    docker {
      image 'sumergerepo/ubi-quarkus-graalvmce-builder-image:jdk-21-rb'
    }
  }

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
            // 自訂 image 名稱和 tag，改成你想要的
            def imageName = "cclemon-auth"
            def imageTag = "latest"

            sh "docker build -t ${imageName}:${imageTag} ."
            // 如果要推到遠端 registry，記得先 docker login
            // sh "docker push ${imageName}:${imageTag}"
          }
        }
      }
    }
  }
}
