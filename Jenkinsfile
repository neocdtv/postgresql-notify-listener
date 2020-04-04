pipeline {
    agent any
    
    tools { 
        maven 'maven-3.6.3' 
    }
    
    parameters { choice(name: 'CHOICES', choices: ['one', 'two', 'three'], description: '') }
    
    stages {
        stage('Build') {
            steps {
                whateverFunction()
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

void whateverFunction() {
    sh 'ls /'
}
