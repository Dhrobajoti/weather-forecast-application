pipeline {
    agent any

    options {
        // This enables the classic stage view
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
    }

    environment {
        DOCKER_REGISTRY = 'index.docker.io/v1/'
        DOCKER_USERNAME = 'dhrobajoti'
        GIT_CREDENTIALS_ID = 'github-credentials'
        DOCKER_CREDENTIALS_ID = 'docker-credentials'
        GIT_REPO = 'https://github.com/Dhrobajoti/weather-forecast-application.git'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', 
                credentialsId: "${env.GIT_CREDENTIALS_ID}", 
                url: "${env.GIT_REPO}"
            }
        }

        stage('Build Docker Images') {
            stages {
                stage('Build frontend-gui') {
                    steps {
                        buildAndPushService('frontend-gui')
                    }
                }
                stage('Build backend-api') {
                    steps {
                        buildAndPushService('backend-api')
                    }
                }
                stage('Build backend-graph') {
                    steps {
                        buildAndPushService('backend-graph')
                    }
                }
                stage('Build backend-csv') {
                    steps {
                        buildAndPushService('backend-csv')
                    }
                }
                stage('Build backend-db') {
                    steps {
                        buildAndPushService('backend-db')
                    }
                }
                stage('Build backend-exporter') {
                    steps {
                        buildAndPushService('backend-exporter')
                    }
                }
            }
        }

        stage('Final Cleanup') {
            steps {
                sh '''
                    docker system prune -af || true
                    docker builder prune -af || true
                    docker volume prune -f || true
                    df -h
                '''
            }
        }
    }

    post {
        always {
            cleanWs()
            sh 'docker system df'
            script {
                currentBuild.description = "Built by ${currentBuild.getBuildCauses()[0].shortDescription}"
            }
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}

def buildAndPushService(String service) {
    def imageName = "${env.DOCKER_USERNAME}/weatherforecastapplication-${service}:latest"
    
    try {
        echo "Building image: ${imageName}"
        docker.build(imageName, "./${service}")

        // Login only once
        if (service == 'frontend-gui') {
            withCredentials([usernamePassword(
                credentialsId: "${env.DOCKER_CREDENTIALS_ID}", 
                passwordVariable: 'DOCKER_PASSWORD', 
                usernameVariable: 'DOCKER_USER'
            )]) {
                sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USER --password-stdin"
            }
        }

        sh "docker push ${imageName}"
    } finally {
        echo "Cleaning up after ${service} build"
        sh """
            docker rmi ${imageName} || true
            docker system prune -f --filter 'until=24h' || true
        """
    }
}