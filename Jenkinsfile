pipeline {
    agent any

    environment {
        DOCKER_REGISTRY = 'index.docker.io/v1/'
        DOCKER_USERNAME = 'dhrobajoti' // Replace with your Docker Hub username
        GIT_CREDENTIALS_ID = 'github-credentials'  // Replace with your GitHub credentials ID in Jenkins
        DOCKER_CREDENTIALS_ID = 'docker-credentials' // Replace with your Docker Hub credentials ID in Jenkins
        GIT_REPO = 'https://github.com/Dhrobajoti/weather-forecast-application.git' // Replace with your Git repository URL
    }

    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', credentialsId: "${env.GIT_CREDENTIALS_ID}", url: "${env.GIT_REPO}"
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    def services = ['frontend-gui', 'backend-api', 'backend-graph', 'backend-csv', 'backend-db', 'backend-exporter']
                    
                    services.each { service ->
                        def imageName = "${DOCKER_USERNAME}/weatherforecastapplication-${service}:latest"
                        echo "Building image: ${imageName}"

                        sh "docker build -t ${imageName} ./${service}"

                        withCredentials([usernamePassword(credentialsId: "${DOCKER_CREDENTIALS_ID}", passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USER')]) {
                            sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USER --password-stdin"
                        }

                        sh "docker push ${imageName}"
                    }
                }
            }
        }    

        stage('Cleanup Docker') {
            steps {
                sh 'docker image prune -f || true'
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
