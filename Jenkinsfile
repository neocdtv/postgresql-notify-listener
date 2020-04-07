pipeline {
    agent any
    
    tools { 
        maven 'maven-3.6.3' 
    }
    
    stages {
        stage('Build') {
            steps {
		parameters {
	 		string(name: 'VERSION', defaultValue: '', description: '')
	    	}
                script {
                    if (!params.VERSION.startsWith("release")) {
			error("Only release branches can be deployed to production!")
		    }
                    if (isValidVersion(params.VERSION)) {
                        branch = getBranch(params.VERSION);
                        tag = params.VERSION;
                        println("branch: " + branch);
                        println("tag:" + tag);
                    } else {
                        error("Provided VERSION is invalid. Expected format is escaptedBranchName_timestamp as generated by the build job and populated to nexus and git as tags. Example: release-v20.1_20200407.072811")
                    }
                }
            }
        }
    }
}

boolean isValidVersion(version) {
    return version.matches(".*_.*");
}

String getBranch(version) {
    escapedBranch = version.split("_")[0];
    branch = escapedBranch.replaceAll("-", "/");
    return branch; 
}

