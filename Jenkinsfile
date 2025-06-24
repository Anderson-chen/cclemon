pipeline {
  agent {
    docker {
      image 'gradle:8.8-jdk21'
      args '-v $HOME/.gradle:/home/gradle/.gradle' // Optional: cache Gradle deps
      // 注意：要確保 Jenkins 容器有權限使用 docker daemon（掛載 /var/run/docker.sock）
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
      agent {
        docker {
          image 'docker:24'  // 這裡用官方 Docker CLI image
          args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
      }
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
