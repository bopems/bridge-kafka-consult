#!/usr/bin/env groovy

node {
		def app
		def novaVersao
	
	try {
		stage('Validar pre-requisitos') {
			sh 'git --version'
			sh 'java -version'
			sh 'mvn -version'
		}
		
		
		stage('Sincronizar e instalar dependências') {
			checkout scm
		}
				
		stage('Clean Project') {
			configFileProvider([configFile(fileId: 'a92fd872-a46b-4dbd-bf41-9ffb186ac4a1', variable: 'MAVEN_SETTINGS')]) {
    			sh 'mvn -s $MAVEN_SETTINGS clean'
			}
		}
		
		stage('Build Project') {
			configFileProvider([configFile(fileId: 'a92fd872-a46b-4dbd-bf41-9ffb186ac4a1', variable: 'MAVEN_SETTINGS')]) {
    			sh 'mvn -s $MAVEN_SETTINGS install'
			}
		}
		
	    stage('Static Code Analysis - SonarQube Scanner') {
			def scannerHome = tool 'SonarQubeScanner';
			withSonarQubeEnv('SonarQubeServer') {
				sh "${scannerHome}/bin/sonar-scanner"
			}
		}
		
		stage("Overall Software Quality - SonarQube Quality Gate") {
			timeout(time: 1, unit: 'HOURS') { // Just in case something goes wrong, pipeline will be killed after a timeout
				def qg = waitForQualityGate() // Reuse taskId previously collected by withSonarQubeEnv
				if (qg.status != 'OK') {
					error "Pipeline aborted due to quality gate failure: ${qg.status}"
				}
			}
		}
		
		stage('Controle de Versão') {
		
			configFileProvider([configFile(fileId: 'a92fd872-a46b-4dbd-bf41-9ffb186ac4a1', variable: 'MAVEN_SETTINGS')]) {
    			
				echo "Versão do Projeto Atual: " + readMavenPom().getVersion()
    			
				result = sh (script: "git log -1 | grep '#sem-versao'", returnStatus: true)
				if (result == 0) {
					echo 'Bumping Version - Skipping'
				} else {
    				result = sh (script: "git log -1 | grep '#major'", returnStatus: true)
					
					if (result == 0) {
						echo "Bumping Version - Major"
    			
		    			sh 'mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.nextMajorVersion}.\\\${parsedVersion.majorVersion}.\\\${parsedVersion.incrementalVersion} versions:commit'
					} else {
						result = sh (script: "git log -1 | grep '#minor'", returnStatus: true)
						
						if (result == 0) {
    						echo "Bumping Version - Minor"
    						
			    			sh 'mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.nextMinorVersion}.\\\${parsedVersion.incrementalVersion} versions:commit'
			    		} else {
							echo "Bumping Version - Patch"
			    				
			    			sh 'mvn build-helper:parse-version versions:set -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} versions:commit'
						}
					}
					
					novaVersao = readMavenPom().getVersion() + '-' + BUILD_NUMBER
					echo 'Nova Versão com Build Number: ' + novaVersao
					
					withCredentials([usernamePassword(credentialsId: 'b23d8b4b-b40a-4c58-88b7-fc76f2860b81', passwordVariable: 'GIT_PASSWORD', usernameVariable: 'GIT_USERNAME')]) {
						sh("git config user.email jenkins")
						sh("git config user.name 'jenkins'")
						sh("git config credential.username ${env.GIT_USERNAME}")
						sh("git config credential.helper '!echo password=\$GIT_PASSWORD; echo'")
						sh("git add package.json")
						sh("GIT_ASKPASS=true git tag -a ${novaVersao} -m 'jenkins - tag [${novaVersao}] [ci-skip]'")
						sh("GIT_ASKPASS=true git push origin --tags")
					}
				}
			}
		}
		
		
		stage('Build image Docker') {
	        app = docker.build("microservico/modulo-nsu-gerador")
	    }
	
	    stage('Test image Docker') {
	        app.inside {
	            sh 'echo "Tests passed"'
	        }
	    }
	    
	    stage('Docker - Push Image') {
			docker.withRegistry('http://docker-registry.bopems.local:5000') {
				app.push("${novaVersao}")
				app.push("latest")
			}
		}
		
		stage('Docker - Deploy') {
			
			echo 'DEPLOY STARTED'
			
			deployStop = sh (script: "ssh jenkinsUser@sep0259pa.sepdsv.local sudo docker stop modulo-nsu-gerador || true", returnStdout: true)
			
			deployRmi = sh (script: "ssh jenkinsUser@sep0259pa.sepdsv.local sudo docker images --no-trunc | grep 'modulo-nsu-gerador' | awk '{ print \$3 }' | xargs -r docker rmi  || true", returnStdout: true)

			deployRm = sh (script: "ssh jenkinsUser@sep0259pa.sepdsv.local sudo docker rm modulo-nsu-gerador || true", returnStdout: true)
			
			echo 'Stopping modulo-nsu-gerador: ' + deployStop
			if (novaVersao) {
				deployPull = sh (script: "ssh jenkinsUser@sep0259pa.sepdsv.local sudo docker pull docker-registry.bopems.local:5000/microservico/modulo-nsu-gerador:${novaVersao}", returnStdout: true)
				deployStart = sh (script: "ssh jenkinsUser@sep0259pa.sepdsv.local sudo docker run -d -p 9052:9052 --name modulo-nsu-gerador docker-registry.bopems.local:5000/microservico/modulo-nsu-gerador:${novaVersao}", returnStdout: true)
	
				echo 'Pulling modulo-nsu-gerador: ' + novaVersao + ': ' + deployPull
			} else {
				deployPull = sh (script: "ssh jenkinsUser@sep0259pa.sepdsv.local sudo docker pull docker-registry.bopems.local:5000/microservico/modulo-nsu-gerador:latest", returnStdout: true)
				deployStart = sh (script: "ssh jenkinsUser@sep0259pa.sepdsv.local sudo docker run -d -p 9052:9052 --name modulo-nsu-gerador docker-registry.bopems.local:5000/microservico/modulo-nsu-gerador:latest", returnStdout: true)

				echo 'Pulling modulo-nsu-gerador:latest : ' + deployPull
			}
			
			echo 'Starting modulo-nsu-gerador: ' + novaVersao + ': ' + deployPull
			
		}
	
	} catch (e) {
		echo "Sending e-mail - SEND_EMAIL_ON_FAIL = ${SEND_EMAIL_ON_FAIL}"
		if (SEND_EMAIL_ON_FAIL == 'true') {
			echo "EMAIL_TO_SISCAP = ${EMAIL_TO_SISCAP}"
			notifyFailed()
		}
		throw e
	}
}


def notifyFailed() {
	emailext (
		replyTo: 'guilherme.salomone@dbccompany.com.br',
		subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
		body: """<p>FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
		<p>Check console output at "<a href="${env.BUILD_URL}">${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>"</p>""",
		to: "${EMAIL_TO_SISCAP}"
	)
}
