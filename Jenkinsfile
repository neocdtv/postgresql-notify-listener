pipeline {
    agent any
    
    tools { 
        maven 'Maven 3.6.3' 
    }
    
    parameters { string(name: 'DEPLOY_ENV', defaultValue: 'staging', description: '') }

    stages {
        stage('Build') {
            steps {
                sh "java -version"
                sh "mvn clean install"
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            steps {
                echo 'Deploying....'
            }
        }
    }
}
