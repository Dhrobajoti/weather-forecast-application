pipeline {
    agent any

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
                git branch: 'main', credentialsId: "${env.GIT_CREDENTIALS_ID}", url: "${env.GIT_REPO}"
            }
        }

        stage('Build and Push Docker Images') {
            steps {
                script {
                    def services = ['frontend-gui', 'backend-api', 'backend-graph', 'backend-csv', 'backend-db', 'backend-exporter']
                    
                    services.each { service ->
                        stage("Build ${service}") {
                            try {
                                def imageName = "${DOCKER_USERNAME}/weatherforecastapplication-${service}:latest"
                                echo "Building image: ${imageName}"

                                // Build the image
                                docker.build(imageName, "./${service}")

                                // Login to Docker Hub once before pushing all images
                                if (service == services[0]) {
                                    withCredentials([usernamePassword(
                                        credentialsId: "${DOCKER_CREDENTIALS_ID}", 
                                        passwordVariable: 'DOCKER_PASSWORD', 
                                        usernameVariable: 'DOCKER_USER'
                                    )]) {
                                        sh "echo \$DOCKER_PASSWORD | docker login -u \$DOCKER_USER --password-stdin"
                                    }
                                }

                                // Push the image
                                sh "docker push ${imageName}"
                            } finally {
                                // Cleanup after each service build
                                echo "Cleaning up after ${service} build"
                                sh """
                                    docker rmi ${DOCKER_USERNAME}/weatherforecastapplication-${service}:latest || true
                                    docker system prune -af || true
                                """
                            }
                        }
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
            sh 'docker system df' // Show docker disk usage
        }
    }
}