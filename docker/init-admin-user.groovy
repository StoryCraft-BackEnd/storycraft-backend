#!groovy

import jenkins.model.*
import hudson.security.*

def instance = Jenkins.getInstance()

def adminUsername = "admin"
def adminPassword = "admin"

println "--> creating local user '${adminUsername}'"

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount(adminUsername, adminPassword)
instance.setSecurityRealm(hudsonRealm)

def strategy = new FullControlOnceLoggedInAuthorizationStrategy()
strategy.setAllowAnonymousRead(false)
instance.setAuthorizationStrategy(strategy)

instance.save()
