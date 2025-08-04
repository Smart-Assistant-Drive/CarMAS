rootProject.name = "CarMAS"

plugins {
    id("com.gradle.develocity") version "3.18.1"
    id("org.danilopianini.gradle-pre-commit-git-hooks") version "2.0.12"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/help/legal-terms-of-use"
        termsOfUseAgree = "yes"
    }
}

gitHooks {
    preCommit {
        tasks("check")
    }
    commitMsg { conventionalCommits() }
    createHooks(true)
}
