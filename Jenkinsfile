pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'GitHub source code will be checked out'
            }
        }

        stage('Build Android App') {
            steps {
                bat 'gradlew.bat assembleDebug'
            }
        }
    }
}
