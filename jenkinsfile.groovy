stage('CI'){
    node {
        def project_path = "spring-boot-samples/spring-boot-sample-atmosphere"
         try {
            
           checkout scm
            //git 'https://github.com/helmutsiegel/jenkins2-spring-boot.git'
            
            
            dir("${project_path}") {
             
                stash name: 'everything',
                      includes: '**'
                      
            
                    bat 'mvn clean verify'
                
            }
            notify('Success')
        } catch (err) {
            notify("Error: ${err}")
            currentBuild.result = 'FAILURE'
        }
        
         dir("${project_path}") {
           
                    publishHTML([allowMissing: true, 
                                alwaysLinkToLastBuild: false, 
                                keepAll: true, 
                                reportDir: 'target/site/jacoco/', 
                                reportFiles: 'index.html', 
                                reportName: 'Code Coverage', 
                                reportTitles: ''])
                                
                    step([$class: 'JUnitResultArchiver', testResults: 'target/surefire-reports/TEST-*xml'])
                    archiveArtifacts artifacts: "target/*.jar", 
                                                followSymlinks: false
                
        }
    }
    
    node {
        bat 'dir'
        bat 'del * /s /f /q'
        unstash 'everything'
        bat 'dir'
    }
}



stage('paralel'){
    input 'Go away?'
    parallel  one: {
        node {
            bat 'dir'
        }
    }, two: {
        node{
            bat 'dir'
        }
    }, three: {
        node{
            bat 'dir'
        }
    }
}

def notify(status){
    emailext (
        from: "jenkins",
        to: "helmut.siegel@gmail.com",
        subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
        body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
    )
}