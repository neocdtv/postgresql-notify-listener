pipeline {
    agent any

    tools { 
        maven 'Maven 3.6.3' 
        jdk 'jdk8' 
    }

    stages {
        stage('Build') {
            steps {
                mvn clean install
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